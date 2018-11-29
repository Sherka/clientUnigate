package uniGate.common

import java.io.{File, PrintWriter}

import akka.actor.Actor
import akka.http.scaladsl.util.FastFuture
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import spray.json._
import uniGate.common.Messages.ListRespone

import scala.concurrent.Future

class OutputData extends Actor with Loggable {

  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  implicit val dispatcher = context.dispatcher

  def receive = {
    case ListRespone(list) => {
      log_info("Writing data into new file")
      val writer = new PrintWriter(new File("Write.txt"))
      writer.write(list.toString)
      sender() ! Messages.ResponseFromUnigate(List())
      log_info("Sended repoet to Actor" + sender().path)
      writer.close()
    }
  }

}
