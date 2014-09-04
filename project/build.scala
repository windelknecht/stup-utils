import sbt._
import Keys._

object build extends Build {
  lazy val scalaVer = "2.11.2"

  lazy val defaultSettings =
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
        "org.scala-lang"                % "scala-library"           % scalaVer                  withSources(),
        "org.scala-lang"                % "scala-reflect"           % scalaVer                  withSources(),
        "org.scala-lang.modules"        % "scala-xml_2.11"          % "1.0.2"                   withSources(),
        "com.typesafe.akka"             % "akka-actor_2.11"         % "2.3.5"                   withSources(),
        // apache
        "org.apache.commons"            % "commons-compress"        % "1.8.1"                   withSources(),
        "org.apache.commons"            % "commons-vfs2"            % "2.0"                     withSources(),
        // logging
        "org.clapper"                   % "grizzled-slf4j_2.11"     % "1.0.2"                   withSources(),
        // refl
        "org.reflections"               % "reflections"             % "0.9.9-RC1"               withSources(),
        // testing
        "org.scalatest"                 % "scalatest_2.11"          % "2.2.2"     % "test"      withSources(),
        "com.typesafe.akka"             % "akka-testkit_2.11"       % "2.3.5"     % "test"      withSources(),
        "junit"                         % "junit"                   % "4.11"      % "test"      withSources(),
        "org.mockito"                   % "mockito-all"             % "1.9.5"     % "test"      withSources()
      ),
      fork in Test := true
      ))

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

