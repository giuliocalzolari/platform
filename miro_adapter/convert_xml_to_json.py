#!/usr/bin/env python
# -*- encoding: utf-8 -*-
"""
Parse image records from a Miro XML export, and save them as individual
JSON documents in S3.

Usage:
  convert_xml_to_json.py --bucket=<BUCKET> --src=<SRC> --dst=<DST>
  convert_xml_to_json.py -h | --help

Options:
  -h --help                 Show this screen.
  --bucket=<BUCKET>         Name of the S3 bucket holding our Miro data.
  --src=<SRC>               Name of the Miro XML export.
  --dst=<DST>               Directory for storing the JSON derivatives.

"""

import json

import boto3
import docopt

from utils import generate_image_records


if __name__ == '__main__':
    args = docopt.docopt(__doc__)

    bucket = args['--bucket']
    src_key = args['--src']
    dst_key = args['--dst']

    if not dst_key.endswith('/'):
        dst_key += '/'

    s3 = boto3.client('s3')

    for image in generate_image_records(bucket=bucket, key=src_key):
        s3.put_object(
            Bucket=bucket,
            Key=f'{dst_key}{image["image_no_calc"]}.json',

            # Adding the separators omits unneeded whitespace in the JSON,
            # giving us smaller files.
            Body=json.dumps(image, separators=(',', ':'))
        )
