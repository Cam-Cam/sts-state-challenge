
name := "sts-state-challenge"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "1.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.0"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"

libraryDependencies +="org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies +="org.slf4j" % "slf4j-simple" % "1.7.5"

//libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.0"
//
//libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.0"
//
//libraryDependencies += "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0"


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}






