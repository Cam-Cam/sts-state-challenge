package training.neo4j.sts

import org.slf4j.Logger
import org.slf4j.LoggerFactory


import training.neo4j.sts.json_motor.JsonParser
import training.neo4j.sts.neo4j_motor._
import training.neo4j.sts.argument_validator.ArgumentValidator
import training.neo4j.sts.sts_concepts.{Component, Event, STSMotor}

object EntryPoint extends App  {

val logger = LoggerFactory.getLogger(classOf[App])
  logger.info("Starting program")
  logger.info("Parsing command line arguments")

  val config: ArgumentValidator = ArgumentValidator(args)


  val neo4j: Neo4jParameters = Neo4jParameters()

  val initFile: String = config.initPath
  val eventFile: String = config.eventPath

  logger.info(s"Creating the list of components from ${initFile}")
  val components: List[Component] = JsonParser.createComponent(JsonParser.readInitFile(initFile))
  logger.info(s"Creating the list of events from ${eventFile}")
  val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile(eventFile))

  logger.info("Initialize a Neo4j driver")
  val stsMotor: STSMotor = STSMotor(neo4j.uri, neo4j.user, neo4j.password)

  logger.info(s"Initialize components")
  stsMotor.init(components)
  logger.info(s"Run events")
  stsMotor.update(events, components)
  logger.info(s"Program ends")


}






