module "sqs_freezeray" {
  source        = "git::https://github.com/wellcometrust/terraform.git//ecs_script_task?ref=v1.0.0"
  task_name     = "sqs_freezeray"
  app_uri       = "${module.ecr_repository_sqs_freezeray.repository_url}:${var.release_ids["sqs_freezeray"]}"
  task_role_arn = "${module.ecs_sqs_freezeray_iam.task_role_arn}"

  cpu    = 1024
  memory = 1024

  env_vars = [
    "{\"name\": \"BUCKET\", \"value\": \"${var.infra_bucket}\"}",
    "{\"name\": \"AWS_DEFAULT_REGION\", \"value\": \"${var.aws_region}\"}",
  ]
}

module "sqs_redrive" {
  source        = "git::https://github.com/wellcometrust/terraform.git//ecs_script_task?ref=v1.0.0"
  task_name     = "sqs_redrive"
  app_uri       = "${module.ecr_repository_sqs_redrive.repository_url}:${var.release_ids["sqs_redrive"]}"
  task_role_arn = "${module.ecs_sqs_redrive_iam.task_role_arn}"

  cpu    = 1024
  memory = 1024

  env_vars = [
    "{\"name\": \"BUCKET\", \"value\": \"${var.infra_bucket}\"}",
    "{\"name\": \"AWS_DEFAULT_REGION\", \"value\": \"${var.aws_region}\"}",
  ]
}
