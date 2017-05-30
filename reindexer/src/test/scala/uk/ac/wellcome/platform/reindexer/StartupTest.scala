package uk.ac.wellcome.platform.reindexer

import com.google.inject.Stage
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import uk.ac.wellcome.test.utils.StartupLogbackOverride

class StartupTest extends FeatureTest with StartupLogbackOverride {

  val server = new EmbeddedHttpServer(
    stage = Stage.PRODUCTION,
    twitterServer = new Server,
    flags = Map(
      "aws.dynamo.reindexTracker.tableName" -> "ReindexTracker",
      "aws.dynamo.miroData.tableName" -> "MiroData",
      "reindex.target.tableName" -> "MiroData"
    )
  )

  test("server starts up correctly") {
    server.assertHealthy()
  }
}