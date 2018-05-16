
name := "sts-state-challenge"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "1.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.0"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}






