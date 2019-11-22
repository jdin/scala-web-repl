ThisBuild/ scalaVersion := "2.12.10"
ThisBuild/ organization := "org.jdin.repl"
ThisBuild/ version := "1.0.0"

val http4sVersion = "0.20.13"

lazy val hello = (project in file("."))
  .settings(
    name := "scala-web-repl"
  )

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.slf4j" % "slf4j-simple" % "1.7.29",
  "org.scala-lang" % "scala-compiler" % "2.12.10",
  "com.google.guava" % "guava" % "28.1-jre"
)

scalacOptions ++= Seq("-Ypartial-unification")
