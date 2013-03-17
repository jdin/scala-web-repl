package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import play.api.cache.Cache;
import play.api.Play.current;

import scala.tools.nsc._
import java.io.File
import scala.tools.nsc.interpreter._

import scala.sys.process._

import models._

object Application extends Controller {
  
  
  /**
   * just an index action
   */
  def index = Action { implicit request =>
    Logger.info("invoking index");
    val interpreter = new MyInterpreter("settings"); // TODO
    val intprId = "" + System.currentTimeMillis();
    Logger.info("saving int with id " + intprId);
    Cache.set(intprId, interpreter);
    Ok(views.html.index()).withSession( "intprId" -> intprId );
  }

  def exec(cmd:String) = Action { request =>
    Logger.info("invoking exec with cmd " + cmd);
    val intprId = request.session.get("intprId").get;
    Logger.info("intprId for this session " + intprId);
    val int = Cache.getAs[MyInterpreter](intprId).get;
    Logger.info("invoking exec with interpreter " + int.hashCode);
    Ok(int.exec(cmd)); // TODO async
  }

  /**
   * Instantiates the websocket connection.
   * It accepts the string evaluates it and sends back the result as a string.
   * (Simulates the REPL)
   */
  def execute = WebSocket.using[String] { request =>
    Logger.info("invoking execute");
    val settings = new Settings
    // TODO do not allow imports for file and socket operations ??
    new File("lib").listFiles.foreach(f => {
      settings.classpath.append(f.getAbsolutePath)
      settings.bootclasspath.append(f.getAbsolutePath)
    })

    val (out, outChannel) = Concurrent.broadcast[String]
    val writer = new JPrintWriter(new ChannelWriter(outChannel), true);
    val intp = new IMain(settings, writer)

    val baos = new java.io.ByteArrayOutputStream();
    val stream = new java.io.PrintStream(baos) {
      override def flush() = { 
        super.flush(); 
        outChannel.push(baos.toString)
        baos.reset
      }
    }

    def onReceive(str:String) =  {
      Logger.info("received command: " + str);
      try {
        Console.withOut(stream)(intp.interpret(str))
      } catch {
        case e : Throwable => outChannel.push("Unknown error: " + e.getMessage)
      } finally {
        stream.flush
      }
    }

    val in = Iteratee.foreach(onReceive)

    val welcome = """Welcome to interactive scala!
      Type in expressions to have them evaluated."""

    (in, Enumerator(welcome) >>> out)
  }
  
}
