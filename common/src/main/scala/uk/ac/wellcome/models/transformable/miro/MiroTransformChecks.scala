package uk.ac.wellcome.models.transformable.miro

import scala.util.matching.Regex

import uk.ac.wellcome.models.transformable.{
  FieldIssues,
  ShouldNotTransformException
}

trait MiroTransformChecks {
  /* If the <image_general_use> or <image_copyright_cleared> fields are
   * missing or don't have have the value 'Y', then we shouldn't expose
   * this image in the Catalogue API.
   */
  private def checkCopyrightCleared(
    data: MiroTransformableData): List[FieldIssues] = {
    val generalCleared = if (data.generalUse.getOrElse("N") != "Y") {
      List(FieldIssues("image_general_use", data.generalUse))
    } else Nil

    val copyrightCleared = if (data.copyrightCleared.getOrElse("N") != "Y") {
      List(FieldIssues("image_copyright_cleared", data.copyrightCleared))
    } else Nil

    generalCleared ++ copyrightCleared
  }

  /* There are a bunch of <image_tech_*> fields that refer to the
   * underlying image file.  If these are empty, there isn't actually a
   * file to retrieve, which breaks the Collection site.  Sometimes this is
   * a "glue" record that refers to multiple images.  e.g. V0011212ETL
   *
   * Eventually it might be nice to collate these -- have all the images
   * in the same API result, but for now, we just exclude them from
   * the API.  They aren't useful for testing image search.
   */
  private def checkImageExists(
    data: MiroTransformableData): List[FieldIssues] =
    if (data.techFileSize.getOrElse(List[String]()).isEmpty) {
      List(
        FieldIssues(
          "image_tech_file_size",
          data.techFileSize,
          Some(
            "Missing image_tech_file_size means there is likely no underlying image")))
    } else Nil

  /* The INNOPAC ID should be an 8-digit number (which may have an 'x' at
   * the end).  If this field is missing or incorrectly formatted, we should
   * discard this image.
   */
  private def checkInnopacID(data: MiroTransformableData): List[FieldIssues] = {
    val pattern = "^[0-9]{7}[0-9x]$".r
    data.innopacID.getOrElse("") match {
      case pattern(_*) => Nil
      case _ => List(FieldIssues("image_innopac_id", data.innopacID))
    }
  }

  private def checkLicense(data: MiroTransformableData): List[FieldIssues] =
    data.useRestrictions match {
      case None =>
        throw ShouldNotTransformException(
          List(
            FieldIssues("image_use_restrictions",
                        data.useRestrictions,
                        Some("No value provided for image_use_restrictions?"))
          ))

      case Some("CC-0") | Some("CC-BY") | Some("CC-BY-NC") | Some(
            "CC-BY-NC-ND") =>
        Nil

      // Any images with this label are explicitly withheld from the API.
      case Some("See Related Images Tab for Higher Res Available") => {
        List(
          FieldIssues("image_use_restrictions",
                      data.useRestrictions,
                      Some("Images with this license are explicitly excluded"))
        )
      }

      // These fields are labelled "[Investigate further]" in Christy's
      // document, so for now we exclude them.
      case Some("Do not use") | Some("Not for external use") | Some(
            "See Copyright Information") | Some("Suppressed from WI site") => {
        List(
          FieldIssues(
            "image_use_restrictions",
            data.useRestrictions,
            Some(
              "Images with this license need more investigation before showing in the API"))
        )
      }

      case _ =>
        List(
          FieldIssues("image_use_restrictions",
                      data.useRestrictions,
                      Some("This license type is unrecognised"))
        )
    }

  /* In the L, V and M collections, any images from certain departments are
   * automatically excluded.
   */
  private def checkLicense(data: MiroTransformableData): List[FieldIssues] =
    data.libraryDept match {
      case Some("Archives and Manuscripts") | Some("Public programmes") => {
        List(FieldIssues("image_library_dept", data.libraryDept))
      }
      case _ => Nil
    }

  val checks: List[(MiroTransformableData) => List[FieldIssues]] = List(
    checkCopyrightCleared,
    checkImageExists,
    checkLicense,
    checkInnopacID
  )
}
