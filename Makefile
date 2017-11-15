include shared.Makefile
include loris/Makefile
include shared_infra/Makefile
include catalogue_api/Makefile
include catalogue_pipeline/Makefile
include miro_preprocessor/Makefile
include monitoring/Makefile
include ontologies/Makefile
include sierra_adapter/Makefile


elasticdump-build:
	$(call build_image,elasticdump,docker/elasticdump/Dockerfile)

elasticdump-deploy: elasticdump-build
	$(call publish_service,elasticdump)

api_docs-build:
	$(call build_image,update_api_docs,docker/update_api_docs/Dockerfile)

api_docs-deploy: api_docs-build
	$(call publish_service,update_api_docs)


nginx-build-api: $(ROOT)/.docker/image_builder
	./builds/docker_run.py --dind -- image_builder --project=nginx --variant=api

nginx-build-loris: $(ROOT)/.docker/image_builder
	./builds/docker_run.py --dind -- image_builder --project=nginx --variant=loris

nginx-build-services: $(ROOT)/.docker/image_builder
	./builds/docker_run.py --dind -- image_builder --project=nginx --variant=services

nginx-build-grafana: $(ROOT)/.docker/image_builder
	./builds/docker_run.py --dind -- image_builder --project=nginx --variant=grafana

## Build images for all of our nginx proxies
nginx-build:	\
	nginx-build-api \
	nginx-build-loris \
	nginx-build-services \
    nginx-build-grafana



nginx-deploy-api: nginx-build-api
	$(call publish_service,nginx_api)

nginx-deploy-loris: nginx-build-loris
	$(call publish_service,nginx_loris)

nginx-deploy-services: nginx-build-services
	$(call publish_service,nginx_services)

nginx-deploy-grafana: nginx-build-grafana
	$(call publish_service,nginx_grafana)

## Push images for all of our nginx proxies
nginx-deploy:	\
	nginx-deploy-api \
	nginx-deploy-loris \
	nginx-deploy-services \
	nginx-deploy-grafana


.docker/sbt_test:
	./builds/build_ci_docker_image.py \
		--project=sbt_test \
		--dir=builds \
		--file=builds/sbt_test.Dockerfile

sbt-test-common: .docker/sbt_test
	PROJECT=common ./builds/test_sbt_project.sh

sbt-test-api: .docker/sbt_test
	PROJECT=api ./builds/test_sbt_project.sh

sbt-test-id_minter: .docker/sbt_test
	PROJECT=id_minter ./builds/test_sbt_project.sh

sbt-test-ingestor: .docker/sbt_test
	PROJECT=ingestor ./builds/test_sbt_project.sh

sbt-test-reindexer: .docker/sbt_test
	PROJECT=reindexer ./builds/test_sbt_project.sh

sbt-test-transformer: .docker/sbt_test
	PROJECT=transformer ./builds/test_sbt_project.sh


.docker/sbt_image_builder:
	./builds/build_ci_docker_image.py \
		--project=sbt_image_builder \
		--dir=builds \
		--file=builds/sbt_image_builder.Dockerfile

sbt-build-api: .docker/sbt_image_builder
	PROJECT=api ./builds/run_sbt_image_build.sh

sbt-build-id_minter: .docker/sbt_image_builder
	PROJECT=id_minter ./builds/run_sbt_image_build.sh

sbt-build-ingestor: .docker/sbt_image_builder
	PROJECT=ingestor ./builds/run_sbt_image_build.sh

sbt-build-reindexer: .docker/sbt_image_builder
	PROJECT=reindexer ./builds/run_sbt_image_build.sh

sbt-build-transformer: .docker/sbt_image_builder
	PROJECT=transformer ./builds/run_sbt_image_build.sh



sbt-deploy-api: sbt-build-api $(ROOT)/.docker/publish_service_to_aws
	$(call publish_service,api)

sbt-deploy-id_minter: sbt-build-id_minter $(ROOT)/.docker/publish_service_to_aws
	$(call publish_service,id_minter)

sbt-deploy-ingestor: sbt-build-ingestor $(ROOT)/.docker/publish_service_to_aws
	$(call publish_service,ingestor)

sbt-deploy-reindexer: sbt-build-reindexer $(ROOT)/.docker/publish_service_to_aws
	$(call publish_service,reindexer)

sbt-deploy-transformer: sbt-build-transformer $(ROOT)/.docker/publish_service_to_aws
	$(call publish_service,transformer)

format: \
	format-terraform \
	format-scala

check-format: format lint-python lint-ontologies
	git diff --exit-code


.PHONY: help format check-format

## Display this help text
help: # Some kind of magic from https://gist.github.com/rcmachado/af3db315e31383502660
	$(info Available targets)
	@awk '/^[a-zA-Z\-\_0-9\/]+:/ {                                      \
		nb = sub( /^## /, "", helpMsg );                                \
		if(nb == 0) {                                                   \
			helpMsg = $$0;                                              \
			nb = sub( /^[^:]*:.* ## /, "", helpMsg );                   \
		}                                                               \
		if (nb)                                                         \
			printf "\033[1;31m%-" width "s\033[0m %s\n", $$1, helpMsg;  \
	}                                                                   \
	{ helpMsg = $$0 }'                                                  \
	width=30                                                            \
	$(MAKEFILE_LIST)
