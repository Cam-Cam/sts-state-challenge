package training.neo4j.sts.argument_validator

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scopt.OptionParser


case class ArgumentValidator(initPath: String = "",
                             eventPath: String = "",
                  )

object ArgumentValidator {

  val parser: OptionParser[ArgumentValidator] = new scopt.OptionParser[ArgumentValidator]("java -jar <name of this jar>")
    {
      head("Create a Neo4j graph of components and update it withe events")

      opt[String]('i', "init_component_path").required().action((x, c) => c.copy(initPath = x)).text("Path to json file with components and dependencies to create")

      opt[String]('e', "events_path").required().action((x, c) => c.copy(eventPath = x)).text("Path to json file with events to modify components.")

    }

  def apply(args: Array[String]): ArgumentValidator = {

    val logger = LoggerFactory.getLogger(classOf[ArgumentValidator])

    parser.parse(args, new ArgumentValidator()) match {
      case Some(cmdOpts) =>
        logger.debug(s"All parameters are valid: $cmdOpts")
        cmdOpts
      case None =>
        logger.debug(s"Invalid command line parameters detected in: $args")
        throw new IllegalArgumentException
    }
  }

  }

