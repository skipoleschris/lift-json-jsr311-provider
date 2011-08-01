import com.sun.tools.internal.xjc.reader.gbind.Sequence
import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "templemore"
  val buildScalaVersion = "2.9.0-1"
  val buildVersion      = "0.2"

  val buildSettings = Defaults.defaultSettings ++
                      Seq (organization := buildOrganization,
                           scalaVersion := buildScalaVersion,
                           version      := buildVersion)
}

object Dependencies {

  val extraResolvers = Seq("Java.net Repository" at "http://download.java.net/maven/2")

  val liftJson = "net.liftweb" %% "lift-json" % "2.4-M3" % "compile"
  val jsr311Api = "javax.ws.rs"  % "jsr311-api" % "1.1.1" % "compile"

  val jerseyCore = "com.sun.jersey" % "jersey-core" % "1.6" % "provided"
  val jerseyServer = "com.sun.jersey" % "jersey-server" % "1.6" % "provided"
  val jerseyClient = "com.sun.jersey" % "jersey-client" % "1.6" % "test"
  val jerseySimpleServer = "com.sun.jersey.contribs" % "jersey-simple-server" % "1.6" % "test"

  val springBeans = "org.springframework" % "spring-beans" % "3.0.5.RELEASE" % "provided"
  val springContext = "org.springframework" % "spring-context" % "3.0.5.RELEASE" % "provided"
  val springWeb = "org.springframework" % "spring-web" % "3.0.5.RELEASE" % "provided"
  val springJersey = "com.sun.jersey.contribs" % "jersey-spring" % "1.6" % "provided"

  val jettyServer = "org.eclipse.jetty" % "jetty-server" % "7.4.5.v20110725" % "test"
  val jettyWebapp = "org.eclipse.jetty" % "jetty-webapp" % "7.4.5.v20110725" % "test"

  val specs2 = "org.specs2" %% "specs2" % "1.5" % "test"

  val coreDeps = Seq(liftJson, jsr311Api, jerseyCore, jerseyServer, jerseyClient, jerseySimpleServer, specs2)
  val springDeps = Seq(springBeans, springContext, springWeb, springJersey, jettyServer, jettyWebapp)
}

object TestProjectBuild extends Build {
  import Dependencies._
  import BuildSettings._

  lazy val parentProject = Project ("parent", file ("."),
                                    settings = buildSettings) aggregate (providerProject, springProject)

  lazy val providerProject = Project ("lift-json-jsr311-provider", file ("provider"),
           settings = buildSettings ++ Seq(resolvers ++= extraResolvers, libraryDependencies ++= coreDeps))

  lazy val springProject = Project ("lift-json-jsr311-spring", file ("spring"),
           settings = buildSettings ++ Seq(resolvers ++= extraResolvers, libraryDependencies ++= coreDeps ++ springDeps)) dependsOn (providerProject)
}
