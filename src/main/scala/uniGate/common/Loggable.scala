package uniGate.common

import akka.actor.Actor
import org.slf4j.LoggerFactory

trait Loggable {
  val logger = LoggerFactory.getLogger(getClass)

  def logName: String = {
    this match {
      case actor: Actor =>
        s"Actor [${actor.self.path.toString}]"
      case _ =>
        this.getClass.getCanonicalName
    }
  }

  def log_info(msg: String ): Unit = {
    logger.info(s"${logName}: ${msg}")
  }

}
