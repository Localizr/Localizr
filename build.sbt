name := """localizr"""

import com.github.play2war.plugin._

version := "1.0-SNAPSHOT"

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.apache.jena" % "apache-jena-libs" % "2.12.0",
  "com.flickr4java" % "flickr4java"      % "2.11",
  "org.apache.jena" % "jena-tdb" % "1.1.0",
  "org.apache.jena" % "jena-iri" % "1.1.0",
  "org.apache.jena" % "jena-sdb" % "1.5.0",
  "org.apache.jena" % "jena-arq" % "2.12.0",
  "org.scribe" % "scribe" % "1.3.2",
  "com.google.code.gson" % "gson" % "2.3"
)
