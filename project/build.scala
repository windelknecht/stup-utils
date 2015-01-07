import sbt._
import Keys._

object build extends Build {
  lazy val scalaVer = "2.11.4"

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
    settings = defaultSettings
      ++ Seq(
        libraryDependencies
        ++= Seq(
          "org.scala-lang"                % "scala-library"           % scalaVer                  withSources(),
          "org.scala-lang"                % "scala-reflect"           % scalaVer                  withSources(),
          "org.scala-lang"               %% "scala-pickling"          % "0.9.1"                   withSources(),
          "org.scala-lang.modules"       %% "scala-xml"               % "1.0.3"                   withSources(),
          "com.typesafe.akka"            %% "akka-actor"              % "2.3.8"                   withSources(),
          // apache
          "org.apache.commons"            % "commons-compress"        % "1.9"                     withSources(),
          "org.apache.commons"            % "commons-vfs2"            % "2.0"                     withSources(),
          // logging
          "org.clapper"                  %% "grizzled-slf4j"          % "1.0.2"                   withSources(),
          // refl
          "org.reflections"               % "reflections"             % "0.9.9"                   withSources(),
          // testing
          "org.scalatest"                %% "scalatest"               % "2.2.3"     % "test"      withSources(),
          "com.typesafe.akka"            %% "akka-testkit"            % "2.3.8"     % "test"      withSources(),
          "org.mockito"                   % "mockito-all"             % "1.10.19"   % "test"      withSources()
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

