package uniGate

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import rest.RestApi

object Main extends App with RequestTimeout {

  val config = ConfigFactory.load()
  val host = config.getString("server.host")
  val port = config.getInt("server.port")

  implicit val system = ActorSystem("uniGateActorSystem")
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val api = new RestApi(system, requestTimeout(config))
  val bindingFuture = Http().bindAndHandle(api.routes, host, port)
}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}