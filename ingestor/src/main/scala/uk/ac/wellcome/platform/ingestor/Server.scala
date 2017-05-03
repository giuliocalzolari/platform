package uk.ac.wellcome.platform.ingestor

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import uk.ac.wellcome.finatra.modules._
import uk.ac.wellcome.platform.ingestor.controllers._
import uk.ac.wellcome.platform.ingestor.modules._

object ServerMain extends Server

class Server extends HttpServer {
  override val name = "uk.ac.wellcome.platform.ingestor Ingestor"
  override val modules = Seq(SQSConfigModule,
                             SQSClientModule,
                             AkkaModule,
                             SQSReaderModule,
                             SQSWorker,
                             ElasticClientModule)

  override def configureHttp(router: HttpRouter) {
    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .add[ManagementController]
  }
}