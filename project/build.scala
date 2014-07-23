import sbt._
import Keys._

object Build extends Build {
  lazy val scalaVer = "2.11.1"

  lazy val defaultSettings =
    Defaults.defaultSettings ++
    Seq(
      organization := "de.windelknecht",
      version := guessVersion,
      scalaVersion := scalaVer,
      scalacOptions := Seq(
        "-feature",
        "-language:existentials,reflectiveCalls,postfixOps,implicitConversions",
        "-unchecked",
        "-deprecation",
        "-encoding", "utf8",
        "-Ywarn-adapted-args"
      )
    )

  lazy val root = Project(
    "stup-utils",
    file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.scala-lang"                % "scala-library"           % scalaVer,
        "org.scala-lang"                % "scala-reflect"           % scalaVer,
        "org.scala-lang.modules"        % "scala-xml_2.11"          % "1.0.2",
        "com.typesafe.akka"             % "akka-actor_2.11"         % "2.3.3",
        // apache
        "org.apache.commons"            % "commons-compress"        % "1.8.1",
        "org.apache.commons"            % "commons-vfs2"            % "2.0",
        // logging
        "org.clapper"                   % "grizzled-slf4j_2.11"     % "1.0.2",
        // refl
        "org.reflections"               % "reflections"             % "0.9.9-RC1",
        // testing
        "org.scalatest"                 % "scalatest_2.11"          % "2.2.0"     % "test",
        "com.typesafe.akka"             % "akka-testkit_2.11"       % "2.3.3"     % "test",
        "junit"                         % "junit"                   % "4.11"      % "test",
        "org.mockito"                   % "mockito-all"             % "1.9.5"     % "test"
      )))

  lazy val testPlugin = Project(
    "testPlugin",
    file("testPlugin"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Seq(
        "org.scala-lang"                % "scala-library"           % scalaVer,
        "org.scala-lang.modules"        % "scala-xml_2.11"          % "1.0.2",
        // logging
        "org.clapper"                   % "grizzled-slf4j_2.11"     % "1.0.2"
      )))
    .dependsOn(root)

  def guessVersion = {
    val glatt   = """(\d+\.\d+)""".r
    val unglatt = """(\d+\.\d+)-(\d+)-.*""".r
    val ver = sbt.Process("git describe --tags").! match {
      case 0 => sbt.Process("git describe --tags").!!.trim()
      case _ => "0.0"
    }
    ver match {
      case unglatt(v1, v2) => s"$v1.$v2"
      case glatt(t) => s"$t.0"
      case t => t
    }
  }
}

