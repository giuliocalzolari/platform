package uk.ac.wellcome.platform.api

import uk.ac.wellcome.models._

trait WorksUtil {

  val canonicalId = "1234"
  val title = "this is the first image title"
  val description = "this is a description"
  val lettering = "some lettering"

  val period = Period("the past")
  val agent = Agent("a person")

  val sourceIdentifier = SourceIdentifier(
    IdentifierSchemes.miroImageNumber,
    "sourceIdentifierFromWorksUtil"
  )

  def createWorks(count: Int,
                  start: Int = 1,
                  visible: Boolean = true): Seq[Work] =
    (start to count).map(
      (idx: Int) =>
        workWith(
          canonicalId = s"${idx}-${canonicalId}",
          title = s"${idx}-${title}",
          description = s"${idx}-${description}",
          lettering = s"${idx}-${lettering}",
          createdDate = Period(s"${idx}-${period.label}"),
          creator = Agent(s"${idx}-${agent.label}"),
          items = List(defaultItem),
          visible = visible
      ))

  def workWith(canonicalId: String, title: String): Work =
    Work(title = Some(title),
         sourceIdentifier = sourceIdentifier,
         identifiers = List(sourceIdentifier),
         canonicalId = Some(canonicalId))

  def workWith(canonicalId: String, title: String, visible: Boolean): Work =
    Work(title = Some(title),
         sourceIdentifier = sourceIdentifier,
         identifiers = List(sourceIdentifier),
         canonicalId = Some(canonicalId),
         visible = visible)

  def workWith(
    canonicalId: String,
    title: String,
    identifiers: List[SourceIdentifier] = List(),
    items: List[Item] = List()
  ): Work =
    Work(title = Some(title),
         sourceIdentifier = sourceIdentifier,
         identifiers = identifiers,
         canonicalId = Some(canonicalId),
         items = items)

  def identifiedWorkWith(
    canonicalId: String,
    title: String,
    thumbnail: Location
  ): Work =
    Work(
      title = Some(title),
      sourceIdentifier = sourceIdentifier,
      identifiers = List(
        SourceIdentifier(IdentifierSchemes.miroImageNumber, "5678")
      ),
      canonicalId = Some(canonicalId),
      thumbnail = Some(thumbnail)
    )

  def workWith(canonicalId: String,
               title: String,
               description: String,
               lettering: String,
               createdDate: Period,
               creator: Agent,
               items: List[Item],
               visible: Boolean): Work =
    Work(
      title = Some(title),
      sourceIdentifier = sourceIdentifier,
      identifiers = List(sourceIdentifier),
      canonicalId = Some(canonicalId),
      description = Some(description),
      lettering = Some(lettering),
      createdDate = Some(createdDate),
      creators = List(creator),
      items = items,
      visible = visible
    )

  def defaultItem: Item = {
    itemWith(
      "item-canonical-id",
      defaultSourceIdentifier,
      defaultLocation
    )
  }

  def defaultSourceIdentifier = {
    SourceIdentifier(IdentifierSchemes.miroImageNumber, "M0000001")
  }

  def defaultLocation: Location = {
    digitalLocationWith(
      "https://iiif.wellcomecollection.org/image/M0000001.jpg/info.json",
      License_CCBY)
  }

  def itemWith(
    canonicalId: String,
    identifier: SourceIdentifier,
    location: Location
  ): Item = Item(
    canonicalId = Some(canonicalId),
    sourceIdentifier = identifier,
    identifiers = List(identifier),
    locations = List(location)
  )

  def digitalLocationWith(url: String, license: License): DigitalLocation = {
    DigitalLocation(locationType = "iiif-image", url = url, license = license)
  }
}
