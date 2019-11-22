package org.jdin.repl

import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.UUID

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.IMain

case class Interpreter(id: UUID) {

  private val settings = {
    val ret = new Settings()
    ret.usejavacp.value = true
    ret
  }

  private val main = new IMain(settings)

  def interpret(code: String): String = {
    val stream = new ByteArrayOutputStream()
    try {
      Console.withOut(new PrintStream(stream))(main.interpret(code))
      stream.toString
    } catch {
      case _: Throwable => stream.toString
    } finally {
      stream.flush()
    }
  }

}
