package models;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import scala.tools.nsc._
import scala.tools.nsc.interpreter._

class MyInterpreter(name:String) {

  lazy val settings = { 
    val s = new Settings
    // TODO do not allow imports for file and socket operations ??
    new File("lib").listFiles.foreach(f => {
      s.classpath.append(f.getAbsolutePath)
      s.bootclasspath.append(f.getAbsolutePath)
    })
    s
  }

  val intp = new IMain(settings)

  def exec(cmd:String):String = {
    val baos = new ByteArrayOutputStream();
    val stream = new PrintStream(baos);
    try {
      Console.withOut(stream)(intp.interpret(cmd));
      baos.toString
    } catch {
      case e : Throwable => "Error: " + e.getMessage
    } finally {
      stream.flush
    }
  }


}

