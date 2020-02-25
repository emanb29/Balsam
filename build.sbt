ThisBuild / organization := "me.ethanbell"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / name := "Balsam"

lazy val commonSettings = List(
  scalacOptions ++= Seq(
    "-encoding", "utf8",
    "-deprecation",
    "-unchecked",
    "-Xlint",
    "-feature",
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Ypartial-unification",
    "-Yrangepos",
  ),
  scalacOptions ++= (scalaVersion.value match {
    case VersionNumber(Seq(2, 13, _*), _, _) =>
      List("-Xfatal-warnings")
    case _ => Nil
  }),
  Compile / console / scalacOptions --= Seq("-deprecation", "-Xfatal-warnings", "-Xlint")
)
