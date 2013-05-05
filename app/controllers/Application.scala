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
    val interpreter = new MyInterpreter("settings"); 
    val intprId = "" + System.currentTimeMillis();
    Logger.info("saving int with id " + intprId);
    Cache.set(intprId, interpreter);
    Ok(views.html.index()).withSession( "interpreter" -> intprId );
  }

  /**
   * Executes the command in the interpreter 
   * and returns the result as a String
   */
  def exec(cmd:String) = Action { request =>
    Logger.debug("invoking exec with cmd " + cmd);
    val intprId = request.session.get("interpreter").get;
    Logger.debug("intprId for this session " + intprId);
    val interpreter = Cache.getAs[MyInterpreter](intprId).get;
    Logger.debug("invoking exec with interpreter " + interpreter.hashCode);
    val futureResult = scala.concurrent.Future {
      interpreter exec cmd
    }
    Async {
      futureResult.map { result => Ok(result) }
    }
  }
}
