package controllers

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.Materializer
import actors._
import play.api.Logger
import play.api.libs.json.JsValue

import scala.concurrent.Future

@Singleton
class WsController @Inject()(cc:ControllerComponents)
                            (implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) with SameOriginCheck {

  val logger = play.api.Logger(getClass)

  def ws = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef { out =>
      FlowActor.props(out)
    }
  }

  def ws2 = WebSocket.acceptOrResult[JsValue, JsValue] { rh =>
    Future.successful(sameOriginCheck(rh) match {
      case false => Left(Forbidden)
      case true => Right(ActorFlow.actorRef { out =>
        FlowActor.props(out)
      })
    })
  }

}

trait SameOriginCheck {

  def logger: Logger

  def sameOriginCheck(rh: RequestHeader): Boolean = {
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        logger.error(s"originCheck: rejecting request because Origin header value $badOrigin is not in the same origin")
        false

      case None =>
        logger.error("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  def originMatches(origin: String): Boolean = {
    origin.contains("localhost:9000") || origin.contains("localhost:19001")
  }

}