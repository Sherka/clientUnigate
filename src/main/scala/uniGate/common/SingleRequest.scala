package uniGate.common

import java.io.IOException

import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.{ByteString, Timeout}
import com.typesafe.config.ConfigFactory
import spray.json.{JsArray, JsValue, JsonReader, deserializationError}
import uniGate.common.Messages.ListRespone
import uniGate.rest.DefaultJsonConverter
import spray.json._

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object SingleRequest {
  def props(implicit timeout: Timeout) = Props(new SingleRequest)
}

class SingleRequest(implicit timeout: Timeout) extends DefaultJsonConverter with Actor with Loggable{

  lazy val output = context.actorOf(Props[OutputData], "OutputData")
  implicit val materializator = ActorMaterializer(ActorMaterializerSettings(context.system))

  val config = ConfigFactory.load()
  val uniGateUri = config.getString("unigate-service.host")
  val uniGatePort = config.getInt("unigate-service.port")
  val icmid = config.getString("unigate-service.icmid")
  val apikey = config.getString("unigate-service.apikey")
  val actorRef = new ListBuffer[ActorRef]

  import akka.pattern.pipe
  import context.dispatcher

  def receive = {
    case Messages.RequestToUnigate => {
      log_info(s"Sending request to ${uniGateUri}:${uniGatePort}")
      actorRef.append(sender())
      Http(context.system).singleRequest(HttpRequest(uri = s"http://${uniGateUri}:${uniGatePort}",
      headers = List(headers.RawHeader(name = "icmid", value = icmid), headers.RawHeader(name = "apikey", value = apikey)))).pipeTo(self)
      }
    case responce: HttpResponse => {
      log_info("recieved some data from scheduler")
      val obj = parseHttpResponce(responce)
      obj.foreach(output ! _)
    }
    case list: Messages.ResponseFromUnigate => {
      log_info("Sended message to Actor " + actorRef.head)
      actorRef.head ! list
      actorRef.clear()
    }
  }

  implicit val modelJsonReader = new JsonReader[List[ListRespone]] {
    override def read(json: JsValue): List[ListRespone] = json match {
      case JsArray(elements) => elements.map(_.convertTo[ListRespone]).toList
      case x => deserializationError("Expected List as JsArray, but got " + x)
    }
  }

  def parseJson(str: String) = {
    str.parseJson.convertTo[List[ListRespone]]
  }

  def parseHttpResponce(response: HttpResponse) = response.status match {
    case OK if (response.entity.contentType == ContentTypes.`application/json`) =>
      Unmarshal(response.entity).to[String].map { jsonString => jsonString match {
        case s: String => s.parseJson.convertTo[ListRespone]
        case _ => Right(parseJson(jsonString))
      }
      }
    case BadRequest => Future.successful(Left(s"Something goes wrong"))
    case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
      val error = s"Request failed with status code ${response.status} and entity $entity"
      Future.failed(new IOException(error))
    }
  }
}
