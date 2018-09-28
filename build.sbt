
name := "eventbox"

version := "1.0"

scalaVersion := "2.12.3"

organization := ""

sources in doc in Compile := List()

isSnapshot := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.17"
)
