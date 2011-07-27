name := "lift-json-jsr311-provider"

version := "0.2"

organization := "templemore"

scalaVersion := "2.9.0-1"

resolvers += "Java.net Repository" at "http://download.java.net/maven/2"

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-json" % "2.4-M3" % "compile",
  "javax.ws.rs"  % "jsr311-api" % "1.1.1" % "compile",
  "com.sun.jersey" % "jersey-core" % "1.6" % "provided",
  "com.sun.jersey" % "jersey-server" % "1.6" % "provided",
  "org.specs2" %% "specs2" % "1.5" % "test",
  "com.sun.jersey" % "jersey-client" % "1.6" % "test",
  "com.sun.jersey.contribs" % "jersey-simple-server" % "1.6" % "test"
)

publishTo := Some(Resolver.file("Local Repo", file((Path.userHome / ".m2" / "repository").toString)))
