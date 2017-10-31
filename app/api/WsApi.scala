package api

import play.api.libs.json.{JsPath, Reads}

object WsApi {
  case class WsApiMethod(name: String) extends AnyVal {
    override def toString: String = name
  }

  implicit val wsApiMethodReads: Reads[WsApiMethod] = {
    JsPath.read[String].map(WsApiMethod)
  }

}


