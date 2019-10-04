
name := "eventbox"

version := "2.1"

scalaVersion := "2.12.3"

organization := "io.github.eventbox"

publishTo := sonatypePublishTo.value

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.17"
)
