package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cache;
import play.api.Play.current;

import models._

import scala.concurrent._
import ExecutionContext.Implicits.global

object Application extends Controller {
  
  /**
   * just an index action
   */
  def index = Action { implicit request =>
    Logger.debug("invoking index");
    val interpreter = new MyInterpreter("settings")
    val intprId = String.valueOf(System.currentTimeMillis())
    Cache.set(intprId, interpreter)
    Ok(views.html.index()).withSession( "interpreter" -> intprId )
  }

  /**
   * Executes the command in the interpreter 
   * and returns the result as a String
   */
  def exec(cmd:String) = Action { request =>
    val interpreter = for {
      id <- request.session.get("interpreter")
      interpreter <- Cache.getAs[MyInterpreter](id)
    } yield interpreter
    Async {
      interpreter match {
        case Some(i) => 
          val futureResult = Future { i exec cmd }
          futureResult map { result => Ok(result) }
        case None => Future { InternalServerError("oops") }
      } 
    }
  }
}
