package uniGate.rest

import spray.json._

trait DefaultJsonConverter extends DefaultJsonProtocol {
  import uniGate.common.Messages._

  implicit val listResponseFormat = jsonFormat1(ListRespone)
}
