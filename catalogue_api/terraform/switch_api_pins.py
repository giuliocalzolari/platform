#!/usr/bin/env python
# -*- encoding: utf-8 -*-

import os
import re

import boto3


TFVARS = 'terraform_api.tfvars'

API_NAMES = ('romulus', 'remus')


def get_current_prod_api():
    """
    Returns the current production API, as defined by our Terraform config.

    Raises a RuntimeError if it discovers an unexpected prod API.
    """
    path = os.path.join(os.path.dirname(os.path.abspath(__file__)), TFVARS)

    # We look for a line of the form:
    #
    #       production_api       = "(remus|romulus)"
    #
    production_api = re.search(
        r'production_api \s*= "(?P<name>[a-z]+)',
        open(path).read()).group('name')

    if production_api not in API_NAMES:
        raise RuntimeError(
            'Found unexpected production_api=%r, expected one of %r' %
            (production_api, '/'.join(API_NAMES))
        )

    return production_api


def get_current_api_versions(name):
    """
    For an API (remus/romulus), return the versions of the API and nginx
    that are running in ECS.
    """
    ecs = boto3.client('ecs')

    resp = ecs.describe_services(
        cluster='api_cluster',
        services=[f'api_{name}_v1']
    )
    services = resp['services']
    assert len(services) == 1

    task_definition = ecs.describe_task_definition(
        taskDefinition = services[0]['taskDefinition']
    )
    containers = task_definition['taskDefinition']['containerDefinitions']
    assert len(containers) == 2

    app_container = [c for c in containers if c['name'] == 'app'][0]
    nginx_container = [c for c in containers if c['name'] == 'nginx'][0]

    app_image = app_container['image'].split(':')[-1]
    nginx_image = nginx_container['image'].split(':')[-1]

    return {'app': app_image, 'nginx': nginx_image}


def write_config(config):
    path = os.path.join(os.path.dirname(os.path.abspath(__file__)), TFVARS)

    longest_key = max(len(k) for k in config)

    with open(path, 'w') as f:
        f.write(
            "# This file is autogenerated.  To change the API pins, run 'make new-api-pins'.\n"
        )
        for key, value in config.items():
            f.write(f'{key.ljust(longest_key)} = "{value}"\n')


if __name__ == '__main__':
    old_api = get_current_prod_api()
    new_api = [name for name in API_NAMES if name != old_api].pop()
    print(f'The current API is {old_api}, the new API will be {new_api}.')

    print('Reading current ECS versions...')
    existing_state = get_current_api_versions(new_api)

    print('Writing new config...')

    new_config = {
        'production_api': new_api,
        f'pinned_{old_api}_app': '',
        f'pinned_{old_api}_nginx': '',
        f'pinned_{new_api}_app': existing_state['app'],
        f'pinned_{new_api}_nginx': existing_state['nginx'],
    }

    write_config(new_config)