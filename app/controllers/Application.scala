package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import scala.tools.nsc._
import java.io.File
import scala.tools.nsc.interpreter._

import scala.sys.process._

import models._

object Application extends Controller {
  
  val ScalaHome =
    new File("which scala".!!).getParentFile.getParentFile.getAbsolutePath

  /**
   * just an index action
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  /**
   * Instantiates the websocket connection.
   * It accepts the string evaluates it and sends back the result as a string.
   * (Simulates the REPL)
   */
  def execute = WebSocket.using[String] { request =>
    val settings = new Settings

    // TODO do not allow imports for file and socket operations ??
    val allowed = List("scala-compiler.jar", "scala-library.jar")
    new File(ScalaHome + "/lib").listFiles.foreach(f => {
        if (allowed.contains(f.getName)) {
          settings.classpath.append(f.getAbsolutePath)
          settings.bootclasspath.append(f.getAbsolutePath)
        }
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

    def onReceive(str:String) = 
        try {
          Console.withOut(stream)(intp.interpret(str))
        } catch {
          case e : Throwable => outChannel.push("Unknown error: " + e.getMessage)
        } finally {
          stream.flush
        }

    val in = Iteratee.foreach(onReceive)

    val welcome = """Welcome to interactive scala!
      Type in expressions to have them evaluated."""

    (in, Enumerator(welcome) >>> out)
  }
  
}
