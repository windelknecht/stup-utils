import sbt.Process._

name := "stup-utils"

version := {
  sbt.Process("git describe --tags").! match {
    case 0 => sbt.Process("git describe --tags").!!.replaceFirst("-", ".").trim()
    case _ => "0.0-initVersion"
  }
}

val ver = "2.11.1"

scalaVersion := ver

scalacOptions := Seq("-unchecked", "-language:existentials,reflectiveCalls,postfixOps,implicitConversions", "-deprecation", "-feature", "-encoding", "utf8")

libraryDependencies ++= Seq(
  "org.scala-lang"                % "scala-library"           % ver,
  "org.scala-lang.modules"        % "scala-xml_2.11"          % "1.0.2",
  "com.typesafe.akka"             % "akka-actor_2.11"         % "2.3.3",
  // testing
  "org.scalatest"                 % "scalatest_2.11"          % "2.2.0"     % "test",
  "com.typesafe.akka"             % "akka-testkit_2.11"       % "2.3.3"     % "test",
  "junit"                         % "junit"                   % "4.11"      % "test",
  "org.mockito"                   % "mockito-all"             % "1.9.5"     % "test"
)

