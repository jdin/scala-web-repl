package org.jdin.repl

import org.http4s.dsl.io._
import org.http4s.implicits._
import java.util.UUID
import java.util.concurrent.{Executors, TimeUnit}

import cats.data.Kleisli
import cats.effect.{ContextShift, IO}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import org.http4s.dsl.io.{->, /, GET, NotFound, Ok, POST, Root}
import org.http4s.{HttpRoutes, Request, Response, ResponseCookie, StaticFile, headers}

import scala.concurrent.ExecutionContext

object Service {
  private val blockingEc =
    ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))

  private implicit val cs: ContextShift[IO] =
    IO.contextShift(ExecutionContext.global)

  private val cacheLoader = new CacheLoader[UUID, Interpreter] {
    override def load(key: UUID): Interpreter = Interpreter(key)
  }

  private val cache: LoadingCache[UUID, Interpreter] =
    CacheBuilder.newBuilder
      .expireAfterAccess(20, TimeUnit.MINUTES)
      .build(cacheLoader)

  private def toString(body: org.http4s.EntityBody[IO]): IO[String] =
    body.map(_.toChar)
      .compile
      .toList
      .map(_.mkString)

  val service: Kleisli[IO, Request[IO], Response[IO]] = HttpRoutes.of[IO] {
    case request@GET -> Root =>
      StaticFile.fromResource("/index.html", blockingEc, Some(request))
        .getOrElseF(NotFound())
    case request@POST -> Root / "interpret" =>
      val uuidString: Either[String, String] = for {
        header <- headers.Cookie.from(request.headers).toRight("Cookie parsing error")
        cookie <- header.values.toList.find(_.name == "repl-id").toRight("Couldn't find repl id cookie")
        uuid <- Right(cookie.content)
      } yield (uuid)
      val uuid = uuidString match {
        case Right(uuid) => UUID.fromString(uuid)
        case _ => UUID.randomUUID()
      }
      val interpreter = cache.get(uuid)
      Ok(toString(request.body).map(interpreter.interpret))
        .map(_.addCookie(ResponseCookie("repl-id", uuid.toString)))
  }.orNotFound
}
