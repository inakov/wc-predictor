name := """wc-predictor-server"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.squeryl" % "squeryl_2.10" % "0.9.6-RC3"
)
