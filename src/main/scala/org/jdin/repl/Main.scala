package org.jdin.repl

import cats.effect.{IO, _}
import cats.implicits._
import org.http4s.server.blaze._

object Main extends IOApp {

  private val PORT = sys.env.getOrElse("PORT", sys.props.getOrElse("http.port", "8080"))

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(PORT.toInt, "0.0.0.0")
      .withHttpApp(Service.service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
