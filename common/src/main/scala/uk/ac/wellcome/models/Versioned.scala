package uk.ac.wellcome.models

import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.gu.scanamo.DynamoFormat
import com.gu.scanamo.error.DynamoReadError

trait Sourced {
  val sourceId: String
  val sourceName: String
}

trait Versioned extends Sourced {
  val version: Int
  val id: String = Versioned.id(sourceName, sourceId)
}

object Versioned {
  implicit def toVersionedDynamoFormatWrapper[T <: Versioned](
    implicit dynamoFormat: DynamoFormat[T]): VersionedDynamoFormatWrapper[T] =
    new VersionedDynamoFormatWrapper[T](dynamoFormat)

  def id(sourceName: String, sourceId: String) = s"$sourceName/$sourceId"
}

class VersionedDynamoFormatWrapper[T <: Versioned](
  dynamoFormat: DynamoFormat[T]) {
  val enrichedDynamoFormat = new DynamoFormat[T] {
    override def read(av: AttributeValue): Either[DynamoReadError, T] =
      dynamoFormat.read(av)

    override def write(t: T): AttributeValue =
      dynamoFormat.write(t).addMEntry("id", new AttributeValue(t.id))
  }
}

trait VersionUpdater[T <: Versioned] {
  def updateVersion(versioned: T, newVersion: Int): T
}
