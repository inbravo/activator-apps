package controllers

import play.api.mvc._
import views.html

class Application extends Controller {

  def employeeIndex = Action { Ok(html.index()) }

  def carIndex = Action { Ok(html.index()) }
}
