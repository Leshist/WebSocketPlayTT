package controllers

import javax.inject._
import play.api.mvc._
import javax.inject.Inject


@Singleton
class HomeController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  val logger = play.api.Logger(getClass)

  // renders index page, from where js app is sending socket messages
  def index = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
}





