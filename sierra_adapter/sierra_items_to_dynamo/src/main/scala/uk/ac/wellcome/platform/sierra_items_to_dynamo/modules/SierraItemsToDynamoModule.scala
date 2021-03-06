package uk.ac.wellcome.platform.sierra_items_to_dynamo.modules

import akka.actor.ActorSystem
import com.twitter.inject.{Injector, TwitterModule}
import uk.ac.wellcome.platform.sierra_items_to_dynamo.services.SierraItemsToDynamoWorkerService

object SierraItemsToDynamoModule extends TwitterModule {
  override def singletonStartup(injector: Injector) {
    val workerService = injector.instance[SierraItemsToDynamoWorkerService]

    workerService.runSQSWorker()

    super.singletonStartup(injector)
  }

  override def singletonShutdown(injector: Injector) {
    info("Terminating Sierra to Dynamo worker")

    val system = injector.instance[ActorSystem]
    val workerService = injector.instance[SierraItemsToDynamoWorkerService]

    workerService.cancelRun()
    system.terminate()
  }
}
