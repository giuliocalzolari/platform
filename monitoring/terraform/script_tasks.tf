module "gatling_loris" {
  source        = "../terraform/ecs_script_task"
  task_name     = "gatling_loris"
  app_uri       = "${module.ecr_repository_gatling.repository_url}:${var.release_ids["gatling"]}"
  task_role_arn = "${module.ecs_gatling_iam.task_role_arn}"

  env_vars = [
    "{\"name\": \"SIMULATION\", \"value\": \"testing.load.LorisSimulation\"}",
    "{\"name\": \"AWS_DEFAULT_REGION\", \"value\": \"${var.aws_region}\"}",
    "{\"name\": \"FAILED_TOPIC_ARN\", \"value\": \"${module.load_test_failure_alarm.arn}\"}",
    "{\"name\": \"RESULTS_TOPIC_ARN\", \"value\": \"${module.load_test_results.arn}\"}",
    "{\"name\": \"S3_BUCKET\", \"value\": \"${aws_s3_bucket.dashboard.id}\"}",
  ]
}

module "gatling_catalogue_api" {
  source        = "../terraform/ecs_script_task"
  task_name     = "gatling_catalogue_api"
  app_uri       = "${module.ecr_repository_gatling.repository_url}:${var.release_ids["gatling"]}"
  task_role_arn = "${module.ecs_gatling_iam.task_role_arn}"

  # In the Catalogue API tests, we've seen issues where Gatling fails to
  # create enough SSL connections.  Giving it more memory is an attempt to
  # alleviate these issues.
  memory = 2048

  env_vars = [
    "{\"name\": \"SIMULATION\", \"value\": \"testing.load.CatalogueApiSimulation\"}",
    "{\"name\": \"AWS_DEFAULT_REGION\", \"value\": \"${var.aws_region}\"}",
    "{\"name\": \"FAILED_TOPIC_ARN\", \"value\": \"${module.load_test_failure_alarm.arn}\"}",
    "{\"name\": \"RESULTS_TOPIC_ARN\", \"value\": \"${module.load_test_results.arn}\"}",
    "{\"name\": \"S3_BUCKET\", \"value\": \"${aws_s3_bucket.dashboard.id}\"}",
  ]
}

module "gatling_digital_experience" {
  source        = "../terraform/ecs_script_task"
  task_name     = "gatling_digital_experience"
  app_uri       = "${module.ecr_repository_gatling.repository_url}:${var.release_ids["gatling"]}"
  task_role_arn = "${module.ecs_gatling_iam.task_role_arn}"

  env_vars = [
    "{\"name\": \"SIMULATION\", \"value\": \"testing.load.DigitalExperienceImageSearch\"}",
    "{\"name\": \"AWS_DEFAULT_REGION\", \"value\": \"${var.aws_region}\"}",
    "{\"name\": \"FAILED_TOPIC_ARN\", \"value\": \"${module.load_test_failure_alarm.arn}\"}",
    "{\"name\": \"RESULTS_TOPIC_ARN\", \"value\": \"${module.load_test_results.arn}\"}",
    "{\"name\": \"S3_BUCKET\", \"value\": \"${aws_s3_bucket.dashboard.id}\"}",
  ]
}