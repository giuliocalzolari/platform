module "vpc_sierra_adapter" {
  source     = "git::https://github.com/wellcometrust/terraform.git//network?ref=v1.0.0"
  cidr_block = "10.60.0.0/16"
  az_count   = "2"
  name       = "sierra_adapter"
}
