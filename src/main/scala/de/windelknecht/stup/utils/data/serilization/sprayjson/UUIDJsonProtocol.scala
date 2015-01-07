package de.windelknecht.stup.utils.data.serilization.sprayjson

import java.util.UUID

import spray.json._

trait UUIDJsonProtocol extends DefaultJsonProtocol {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(obj: UUID): JsValue = JsString(obj.toString())

    def read(json: JsValue): UUID = json match {
      case JsString(x) => UUID.fromString(x)
      case _ => deserializationError("Expected UUID as JsString")
    }
  }
}
