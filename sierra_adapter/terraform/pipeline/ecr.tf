module "ecr_repository_sierra_to_dynamo" {
  source = "git::https://github.com/wellcometrust/terraform.git//ecr?ref=v1.0.0"
  name   = "sierra_${var.resource_type}_to_dynamo"
}

module "ecr_repository_sierra_merger" {
  source = "git::https://github.com/wellcometrust/terraform.git//ecr?ref=v1.0.0"
  name   = "sierra_${local.resource_type_singular}_merger"
}