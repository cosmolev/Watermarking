package controllers

import models.Doc.{documentWrites,md5}
import models._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.libs.Akka
import views._

import scala.concurrent.duration._

class Application extends Controller {

  def list() = Action {implicit request =>
    Ok(html.list(Doc.findAll()))
  }

  def newDocument() = Action {request =>
    val title = request.body.asFormUrlEncoded.get.get("title").head.head
    val author = request.body.asFormUrlEncoded.get.get("author").head.head
    val content = request.body.asFormUrlEncoded.get.get("content").head.head
    val topic = content match {
      case "book" => request.body.asFormUrlEncoded.get.get("topic").head.head
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
