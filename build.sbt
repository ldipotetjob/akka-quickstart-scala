import BuildEnvPlugin.autoImport.{configfilegenerator}

import CommandAkkaScheduler.buildAll

lazy val akkaVersion = "2.5.6"

lazy val commonSettings = Seq(
  name:= "akka-quickstart-scala",
  organization := "mojitoverde",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.12.2",
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  )
)

lazy val internetOfThings = (project in file("modules/Iot")).
  settings(commonSettings)

lazy val root = (project in file(".")).
  aggregate(internetOfThings).
  aggregate(AkkaSchedulerPoc).
  settings(commonSettings,commands ++= Seq(buildAll))

/**
  *
  * A global setting onLoad is of type State => State and is executed once, after all projects are built and loaded.
  * So after every thing have been load we seet root as a main project inthis multiproject
 */

onLoad in Global ~= (_ andThen ("project root" :: _))

lazy val AkkaSchedulerPoc = (project in file("modules/AkkaScheduler")).
  enablePlugins(JavaAppPackaging).
  settings(commonSettings,libraryDependencies ++= Seq(
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.1-akka-2.5.x"
 ),resourceGenerators in Compile += configfilegenerator)

fork in run := true