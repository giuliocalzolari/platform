package uk.ac.wellcome.models

import com.sksamuel.elastic4s._
import uk.ac.wellcome.utils.JsonUtil

/** Represents a set of identifiers as stored in DynamoDB */
case class Identifier(CanonicalID: String, MiroID: String)

/** An identifier received from one of the original sources */
case class SourceIdentifier(source: String, sourceId: String, value: String)

case class IdentifiedWork(canonicalId: String, work: Work)

/** A representation of an item in our ontology, without a canonical identifier */
case class Work(identifiers: List[SourceIdentifier],
                       label: String,
                       accessStatus: Option[String] = None)

object IdentifiedWork extends Indexable[IdentifiedWork] {
  override def json(t: IdentifiedWork): String =
    JsonUtil.toJson(t).get
}