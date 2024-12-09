import sbt.Keys.libraryDependencies
import sbtbuildinfo.BuildInfoPlugin.autoImport.buildInfoPackage


lazy val Http4sVersion = "0.23.29"
lazy val CirceVersion = "0.14.10"
lazy val MunitVersion = "1.0.2"
lazy val LogbackVersion = "1.5.12"
lazy val MunitCatsEffectVersion = "2.0.0"
lazy val ScalaVersion = "3.5.2"

lazy val projectSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoOptions += BuildInfoOption.ToMap,
  organization := "com.miloszjakubanis",
  scalaVersion := ScalaVersion,
  libraryDependencies ++= Seq(
    //CLI Arguments
    "com.github.scopt" %% "scopt" % "4.1.0",
    //Config Lib
    "com.typesafe" % "config" % "1.4.3",

    "org.http4s"      %% "http4s-ember-server" % Http4sVersion,
    "org.http4s"      %% "http4s-ember-client" % Http4sVersion,
    "org.http4s"      %% "http4s-circe"        % Http4sVersion,
    "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
    "io.circe" %% "circe-generic" % "0.14.7",
    "io.circe" %% "circe-parser" % "0.14.9",
    "org.scalameta"   %% "munit"               % MunitVersion           % Test,
    "org.typelevel"   %% "munit-cats-effect"   % MunitCatsEffectVersion % Test,
    "ch.qos.logback"  %  "logback-classic"     % LogbackVersion         % Runtime,

    "org.tpolecat" %% "doobie-core" % "1.0.0-RC5",
    "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC5",
    "org.tpolecat" %% "doobie-hikari"   % "1.0.0-RC5",


    "com.typesafe.slick" %% "slick" % "3.5.2",
//    "com.typesafe.slick" %% "slick-pg" % "3.5.2",
    "org.postgresql" % "postgresql" % "42.7.4",
  ),
  testFrameworks += new TestFramework("utest.runner.Framework"),
//  scalacOptions ++= Seq(
//    "-feature",
//    "-language:implicitConversions",
//    "-deprecation",
//    "-unchecked"
//  ),
//  resolvers := {
//    Seq(
//      "releases" at s"https://repo.repsy.io/mvn/og_pixel/earth",
//      "snapshots" at s"https://repo.repsy.io/mvn/og_pixel/moon",
//    )
//  },
)

lazy val root: Project = project
  .in(file("."))
  .enablePlugins(PackPlugin, BuildInfoPlugin)
  .settings(
    projectSettings,
    name := "filebrowser",
    scalaVersion := ScalaVersion,
    version := "0.1.0-SNAPSHOT",
    buildInfoPackage := "filebrowser",
    buildInfoKeys ++= Seq[BuildInfoKey]("appName" -> "filebrowser"),
    buildInfoPackage := "filebrowser"
  )
