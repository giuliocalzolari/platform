module "drain_ecs_container_instance" {
  source = "drain_ecs_container_instance"

  ec2_terminating_topic_publish_policy = "${module.ec2_terminating_topic.publish_policy}"
  ec2_terminating_topic_arn            = "${module.ec2_terminating_topic.arn}"

  lambda_error_alarm_arn = "${module.lambda_error_alarm.arn}"
}

module "ecs_ec2_instance_tagger" {
  source = "ecs_ec2_instance_tagger"

  bucket_infra_id  = "${aws_s3_bucket.infra.id}"
  bucket_infra_arn = "${aws_s3_bucket.infra.arn}"

  ecs_container_instance_state_change_name = "${aws_cloudwatch_event_rule.ecs_container_instance_state_change.name}"
  ecs_container_instance_state_change_arn  = "${aws_cloudwatch_event_rule.ecs_container_instance_state_change.arn}"

  lambda_error_alarm_arn = "${module.lambda_error_alarm.arn}"
}

module "run_ecs_task" {
  source = "run_ecs_task"

  lambda_error_alarm_arn = "${module.lambda_error_alarm.arn}"
}

module "service_scheduler" {
  source = "service_scheduler"

  service_scheduler_topic_publish_policy = "${module.service_scheduler_topic.publish_policy}"
  lambda_error_alarm_arn                 = "${module.lambda_error_alarm.arn}"
}

module "update_ecs_service_size" {
  source = "update_ecs_service_size"

  service_scheduler_topic_arn = "${module.service_scheduler_topic.arn}"

  lambda_error_alarm_arn = "${module.lambda_error_alarm.arn}"
}
