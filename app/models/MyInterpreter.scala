package models;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import scala.tools.nsc._
import scala.tools.nsc.interpreter._

class MyInterpreter(name:String) {

  lazy val settings = { 
    val s = new Settings
    val jars = List("scala-compiler.jar", "scala-library.jar")
    def findLibs(f:File):Unit = 
      if(f.isDirectory) f.listFiles.foreach(findLibs)
      else if (jars.contains(f.getName)) {
        s.classpath.append(f.getAbsolutePath)
        s.bootclasspath.append(f.getAbsolutePath)
      }
    findLibs(new File("."))
    // TODO do not allow imports for file and socket operations ??
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

