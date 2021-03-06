package uk.ac.wellcome.platform.idminter.models

import com.google.inject.Inject
import com.twitter.inject.annotations.Flag
import scalikejdbc._

class IdentifiersTable @Inject()(
  @Flag("aws.rds.identifiers.database") database: String,
  @Flag("aws.rds.identifiers.table") table: String)
    extends SQLSyntaxSupport[Identifier] {
  override val schemaName = Some(database)
  override val tableName = table
  override val useSnakeCaseColumnName = false
  override val columns = Seq(
    "CanonicalId",
    "OntologyType",
    "SourceSystem",
    "SourceId"
  )

  val i = this.syntax("i")
}
