module "vpc_monitoring" {
  source     = "git::https://github.com/wellcometrust/terraform.git//network?ref=v1.0.0"
  cidr_block = "10.40.0.0/16"
  az_count   = "2"
  name       = "monitoring"
}
