name := """Watermarking"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  cache,
  ws,
  specs2 % Test,
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
