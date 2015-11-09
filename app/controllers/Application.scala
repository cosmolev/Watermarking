package controllers

import models.Doc.{documentWrites,md5}
import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Request, AnyContent, Action, Controller}
import play.libs.Akka
import views._

import scala.concurrent.duration._

class Application extends Controller {

  def list() = Action {implicit request =>
    Ok(html.list(Doc.findAll()))
  }

  def getParam(key : String, request : Request[AnyContent]): String = request.body.asFormUrlEncoded.get.get(key).head.head

  def newDocument() = Action {request =>
    val title = getParam("title",request)
    val author = getParam("author",request)
    val content = getParam("content",request)
    val topic = content match {
      case "book" => getParam("topic",request)
      case _ => null
    }

    try {
      val id :Int = Doc.createDocument(content,title,author,Option(topic))
      Akka.system.scheduler.scheduleOnce(10.seconds) {
        Doc.setWatermark(id, md5(Doc.findById(id)) )
      }
      Redirect(routes.Application.list()).flashing("createdId" -> id.toString)
    } catch {case e: Exception =>
      Redirect(routes.Application.list()).flashing("error" -> "An error occured.")
    }


  }

  def showDocument(id: Int) = Action {
    Ok( Json.toJson(Doc.findById(id)) ).as("application/json")
  }

}
