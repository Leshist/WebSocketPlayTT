name := "ws"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.6" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test
