lazy val supportedScalaVersions = List("2.13.1", "2.12.10")

ThisBuild / organization := "me.ethanbell"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / name := "Balsam"

val catsV       = "2.1.0"
val mouseV      = "0.24"
val zioV        = "1.0.0-RC17"
val scalaTestV  = "3.1.1"
val scalaCheckV = "1.14.1"

lazy val commonSettings = List(
  resolvers += "BitChunk GitHub resolver" at "https://maven.pkg.github.com/emanb29/BitChunk",
  scalacOptions ++= Seq(
        "-encoding",
        "utf8",
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
  libraryDependencies ++= Seq(
        "org.typelevel"  %% "cats-core"  % catsV,
        "org.typelevel"  %% "mouse"      % mouseV,
        "dev.zio"        %% "zio"        % zioV,
        "me.ethanbell"   %% "bitchunk"   % "0.1.0-SNAPSHOT",
        "org.scalatest"  %% "scalatest"  % scalaTestV % "test",
        "org.scalacheck" %% "scalacheck" % scalaCheckV % "test",
      ),
  crossScalaVersions := supportedScalaVersions,
  scalacOptions ++= (scalaVersion.value match {
        case VersionNumber(Seq(2, 12, _*), _, _) | VersionNumber(Seq(2, 13, _*), _, _) =>
          List("-Xfatal-warnings")
        case _ => Nil
      }),
  scalacOptions --= (scalaVersion.value match {
        case VersionNumber(Seq(2, 13, _*), _, _) =>
          List("-Ypartial-unification")
        case _ => Nil
      }),
  Compile / console / scalacOptions --= Seq("-deprecation", "-Xfatal-warnings", "-Xlint"),
  scalafmtOnCompile := true,
)
lazy val balsam = (project in file(".")).settings(commonSettings)
