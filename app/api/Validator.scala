package api

import api.WsApi._
import play.api.libs.json.{JsPath, JsResult, JsValue, Reads}

import scala.util.{Failure, Success}

class Validator {
  def validateMsgHasType(msg: JsValue): JsResult[String] = {
    val typeReads: Reads[String] = (JsPath \ "$type").read[String]
    msg.validate[String](typeReads)
  }

  def validateMsgHasAllNeededFields(msg: JsValue): Either[Success[JsValue], Failure[JsValue]] = ???
}
