module "items_window_generator" {
  source = "sierra_window_generator"

  resource_type = "items"

  window_length_minutes    = 30
  trigger_interval_minutes = 15

  lambda_error_alarm_arn = "${local.lambda_error_alarm_arn}"
}

module "items_reader" {
  source = "sierra_reader"

  resource_type = "items"

  bucket_name        = "${aws_s3_bucket.sierra_adapter.id}"
  windows_topic_name = "${module.items_window_generator.topic_name}"

  sierra_fields = "${var.sierra_items_fields}"

  sierra_api_url      = "${var.sierra_api_url}"
  sierra_oauth_key    = "${var.sierra_oauth_key}"
  sierra_oauth_secret = "${var.sierra_oauth_secret}"

  release_id         = "${var.release_ids["sierra_reader"]}"
  ecr_repository_url = "${module.ecr_repository_sierra_reader.repository_url}"

  cluster_name = "${module.sierra_adapter_cluster.cluster_name}"
  vpc_id       = "${module.vpc_sierra_adapter.vpc_id}"

  alb_server_error_alarm_arn = "${local.alb_server_error_alarm_arn}"
  alb_client_error_alarm_arn = "${local.alb_client_error_alarm_arn}"
  alb_cloudwatch_id          = "${module.sierra_adapter_cluster.alb_cloudwatch_id}"
  alb_listener_http_arn      = "${module.sierra_adapter_cluster.alb_listener_http_arn}"
  alb_listener_https_arn     = "${module.sierra_adapter_cluster.alb_listener_https_arn}"

  dlq_alarm_arn          = "${data.terraform_remote_state.shared_infra.dlq_alarm_arn}"
  lambda_error_alarm_arn = "${local.lambda_error_alarm_arn}"

  account_id = "${data.aws_caller_identity.current.account_id}"
}

module "items_to_dynamo" {
  source = "items_to_dynamo"

  demultiplexer_topic_name = "${module.items_reader.topic_name}"
  release_id               = "${var.release_ids["sierra_items_to_dynamo"]}"

  cluster_name = "${module.sierra_adapter_cluster.cluster_name}"
  vpc_id       = "${module.vpc_sierra_adapter.vpc_id}"

  alb_server_error_alarm_arn = "${local.alb_server_error_alarm_arn}"
  alb_client_error_alarm_arn = "${local.alb_client_error_alarm_arn}"
  alb_cloudwatch_id          = "${module.sierra_adapter_cluster.alb_cloudwatch_id}"
  alb_listener_http_arn      = "${module.sierra_adapter_cluster.alb_listener_http_arn}"
  alb_listener_https_arn     = "${module.sierra_adapter_cluster.alb_listener_https_arn}"

  dlq_alarm_arn          = "${data.terraform_remote_state.shared_infra.dlq_alarm_arn}"
  lambda_error_alarm_arn = "${local.lambda_error_alarm_arn}"

  account_id = "${data.aws_caller_identity.current.account_id}"
}

module "items_merger" {
  source = "merger"

  resource_type = "items"

  release_id = "${var.release_ids["sierra_item_merger"]}"

  merged_dynamo_table_name = "${local.vhs_table_name}"

  updates_topic_name = "${module.items_to_dynamo.topic_name}"

  cluster_name = "${module.sierra_adapter_cluster.cluster_name}"
  vpc_id       = "${module.vpc_sierra_adapter.vpc_id}"

  alb_server_error_alarm_arn = "${local.alb_server_error_alarm_arn}"
  alb_client_error_alarm_arn = "${local.alb_client_error_alarm_arn}"
  alb_cloudwatch_id          = "${module.sierra_adapter_cluster.alb_cloudwatch_id}"
  alb_listener_http_arn      = "${module.sierra_adapter_cluster.alb_listener_http_arn}"
  alb_listener_https_arn     = "${module.sierra_adapter_cluster.alb_listener_https_arn}"

  dlq_alarm_arn = "${data.terraform_remote_state.shared_infra.dlq_alarm_arn}"

  account_id = "${data.aws_caller_identity.current.account_id}"

  vhs_full_access_policy = "${local.vhs_full_access_policy}"
  bucket_name            = "${local.vhs_bucket_name}"
}
