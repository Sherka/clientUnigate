package uniGate.rest

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import uniGate.common.SingleRequest

import scala.concurrent.ExecutionContext

class RestApi(system: ActorSystem, timeout: Timeout) extends SingleRequestApi {

  implicit val requestTimeout = timeout
  implicit val executionContext = system.dispatcher

  def createSingleRequest = system.actorOf(SingleRequest.props, "SingleRequest")
  def routes: server.Route = getObservation

  def getObservation = {
    path("get-messages") {
      get {
        //GET /get
        onSuccess(getObservationFromUnigate()) { res =>
          complete("Its OK")
        }
      }
    }
  }

//  def getObservationWithId = {
//    pathPrefix("get" / IntNumber) { observationId =>
//      pathEnd{
//        get {
//          //GET /get/{id}
//          onSuccess(getObservationWithIdFromUnigate(observationId)) {
//            list => complete()
//          }
//        }
//      }
//    }
//  }

}

trait SingleRequestApi {
  import uniGate.common.Messages._
  def createSingleRequest(): ActorRef
  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val singleRequest = createSingleRequest()

  def getObservationFromUnigate() = {
    singleRequest.ask(RequestToUnigate).mapTo[ResponseFromUnigate]
  }
//  def getObservationWithIdFromUnigate(id: Int) = {
//    singleRequest.ask(RequestWithIdToUnigate(id)).mapTo[ResponseFromUnigate]
//  }

}
