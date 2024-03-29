name := "sgit"

version := "0.1"

scalaVersion := "2.11.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % "test"

libraryDependencies += "org.mockito" %% "mockito-scala" % "1.5.18"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

parallelExecution in Test := false

lazy val sgit = (project in file("."))
  .enablePlugins(JavaAppPackaging)

maintainer := "your.name@company.org"