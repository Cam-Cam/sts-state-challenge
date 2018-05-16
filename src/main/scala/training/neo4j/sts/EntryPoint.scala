package training.neo4j.sts

import training.neo4j.sts.jsonMotor.JsonParser
import training.neo4j.sts.neo4j_motor._
import training.neo4j.sts.argument_validator.Config
import training.neo4j.sts.sts_concepts.{Component, Event, STSMotor}

object EntryPoint extends App {

  val config: Config = Config(args)

  val neo4j: Neo4jParameters = Neo4jParameters()

  val initFile: String = config.initPath
  val eventFile: String = config.eventPath

  val components: List[Component] = JsonParser.createComponent(JsonParser.readInitFile(initFile))
  val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile(eventFile))


  val stsMotor: STSMotor = STSMotor(neo4j.uri, neo4j.user, neo4j.password)
  stsMotor.init(components)
  stsMotor.update(events, components)


}






