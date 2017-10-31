package actors

import play.api.libs.json.{JsValue, Json}

import api.WsApi._
import api.AccessLevels._
import api.AccessCodes._
import api.TableSubscribeModes._
import api.Validator
import tables.TableStorage
import akka.actor._
import play.api.libs.json._
import db.Database

/**
  actor that provides Flow[JsValue, JsValue] for WebSocket
  has inside objects:
  Validator(validates incoming messages)
  and
  TableStorage(stores state of websocket user Session, what tables to show, etc),
  also creates Database(simple db mock) instance when needed
 */
class FlowActor(out: ActorRef) extends Actor {
  private val validator = new Validator
  private lazy val tables = new TableStorage
  private var accessLevel: AccessLevel = User
  private var tablesSubscription: SubscriptionMode = UnSubscribed

  def receive = {
    case msg: JsValue => handleMsg(msg)
    case msg => out ! Json.obj("msg" -> s"unhandled msg $msg only json pls")
  }

  private def respondToMessage(msg: JsValue, method: WsApiMethod): Unit = method match {
    case WsApiMethod("ping")               => respondToPing(msg)
    case WsApiMethod("login")              => respondToLogin(msg)
    case WsApiMethod("subscribe_tables")   => respondToSubsribeToTables(msg)
    case WsApiMethod("unsubscribe_tables") => respondToUnsubscribeFromTables(msg)

    // access demanding methods
    case WsApiMethod("add_table")          => respondToAddTable(msg)
    case WsApiMethod("update_table")       => respondToUpdateTable(msg)
    case WsApiMethod("remove_table")       => respondToRemoveTable(msg)


    case unSupportedMethod => Json.obj("this method $type is not supported: " -> unSupportedMethod.name)
  }

  private def getMsgType(msg: JsValue): WsApiMethod = (msg \ "$type").as[WsApiMethod]

  private def validateMsg(msg: JsValue): JsResult[String] = {
    val typeReads: Reads[String] = (JsPath \ "$type").read[String]
    msg.validate[String](typeReads)
  }

  private def handleMsg(msg: JsValue): Unit = {
    validator.validateMsgHasType(msg) match {
      case s: JsSuccess[String] => respondToMessage(msg, getMsgType(msg))
      case e: JsError           => out ! Json.obj("error" -> "message should have $type")
    }
  }

  private def respondToPing(msg: JsValue): Unit = out ! (msg.as[JsObject] ++ Json.obj("$type" -> "pong"))

  private def respondToLogin(msg: JsValue): Unit = {
    val username = (msg \ "username").as[String]
    val password = (msg \ "password").as[String]

    val db = new Database

    if (db.isAdmin(username, password)) {
      grantAdminAccess()
      out ! Json.obj("$type" -> "login_successful", "user_type" -> accessLevel.toString)
    }

    else out ! Json.obj("$type" -> "login_failed")
  }

  private def respondToSubsribeToTables(msg: JsValue): Unit = {
    subscribeToTables()
    val userTables = tables.getTables
    out ! (userTables.as[JsObject] ++ Json.obj("$type" -> "table_list"))
  }

  private def respondToUnsubscribeFromTables(msg: JsValue): Unit = {
    unSubscribeFromTables()
    out ! Json.obj("$type" -> "unsubscribe_tables")
  }

  private def respondToAddTable(msg: JsValue): Unit = {
    getAccessCode match {
      case Allowed =>
        if (tablesSubscription == Subscribed) out ! tables.addTableAndReturnMsg(msg)
      case Forbidden => out ! Json.obj("$type" -> "not_authorized")
    }
  }

  private def respondToUpdateTable(msg: JsValue): Unit = {
    getAccessCode match {
      case Allowed =>
        if (tablesSubscription == Subscribed) out ! tables.updateTableAndReturnMsg(msg)
      case Forbidden => out ! Json.obj("$type" -> "not_authorized")
    }
  }

  private def respondToRemoveTable(msg: JsValue): Unit = {
    getAccessCode match {
      case Allowed =>
        if (tablesSubscription == Subscribed) out ! tables.removeTableAndReturnMsg(msg)
      case Forbidden => out ! Json.obj("$type" -> "not_authorized")
    }
  }


  private def getAccessCode          : AccessCode = if (accessLevel   == Admin) Allowed else Forbidden
  private def grantAdminAccess     (): Unit       = accessLevel        = Admin
  private def subscribeToTables    (): Unit       = tablesSubscription = Subscribed
  private def unSubscribeFromTables(): Unit       = tablesSubscription = UnSubscribed

}

object FlowActor {
  def props(out: ActorRef) = Props(new FlowActor(out))
}