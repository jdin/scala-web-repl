package org.jdin.repl

import cats.effect.{IO, _}
import cats.implicits._
import org.http4s.server.blaze._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(Service.service)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
