package controllers

import play.api._
import play.api.mvc._

object Lessons extends Controller {
  
  lazy val lessons = {
    import views.html.lessons._;
    List(lesson1(), lesson2(), lesson3())
  }

  def next = Action { request =>
    Logger.debug("request for next page ")
    val i = request.session.get("lesson").getOrElse[String]("0").toInt
    val intprId = request.session.get("interpreter").get;
    Ok(lessons(i)).withSession(
      "lesson" -> {
        val j = i + 1
        if (j == lessons.size) (j - 1).toString // show last lesson
        else j.toString
      },
      "interpreter" -> intprId
    )
  }
}
