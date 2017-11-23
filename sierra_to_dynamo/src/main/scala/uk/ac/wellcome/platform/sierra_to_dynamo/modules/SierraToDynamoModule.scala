package uk.ac.wellcome.platform.sierra_to_dynamo.modules

import akka.actor.ActorSystem
import com.twitter.inject.{Injector, TwitterModule}
import uk.ac.wellcome.utils.GlobalExecutionContext.context
import uk.ac.wellcome.utils.TryBackoff

object SierraToDynamoModule extends TwitterModule with TryBackoff {

  override def singletonStartup(injector: Injector) {
    info("Starting SierraToDynamo module")

    val actorSystem = injector.instance[ActorSystem]
    run(() => start(), actorSystem)
  }

  private def start() = {

  }

  override def singletonShutdown(injector: Injector) {
    info("Terminating SierraToDynamo")
    cancelRun()
    val system = injector.instance[ActorSystem]
    system.terminate()
  }
}
