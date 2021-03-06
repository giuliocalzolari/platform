package uk.ac.wellcome.models

case class UnidentifiableException()
    extends Exception("Can't find canonicalId for this Identifiable")

trait Identifiable {
  val canonicalId: Option[String]
  def id: String = canonicalId.getOrElse(
    throw UnidentifiableException()
  )
  val sourceIdentifier: SourceIdentifier
  val ontologyType: String
}
