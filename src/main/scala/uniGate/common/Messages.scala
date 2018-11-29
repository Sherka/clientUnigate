package uniGate.common

object Messages {
  case object RequestToUnigate
  case class RequestToWithId(id: Int)

  case class ResponseFromUnigate(list: List[String])

  case class ListRespone(list: List[String])
}
