package uk.ac.wellcome.platform.sierra_to_dynamo.kitchen

import org.scalatest.FunSpec
import uk.ac.wellcome.test.utils.DynamoDBLocal

class SierraSinkTest
    extends FunSpec
    with DynamoDBLocal {

  it("should retry if the target service returns a failed future") {
    true shouldBe false
  }
}
