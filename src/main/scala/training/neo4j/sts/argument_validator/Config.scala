package training.neo4j.sts.argument_validator

import scopt.OptionParser

case class Config (initPath: String = "",
                   eventPath: String = ""
                  )

object Config {

  val parser: OptionParser[Config] = new scopt.OptionParser[Config]("java -jar <name of this jar>")
    {
      head("Create a Neo4j graph of components and update it withe events")

      opt[String]('i', "init_component_path").required().action((x, c) => c.copy(initPath = x)).text("Path to json file with components and dependencies to create")

      opt[String]('e', "events_path").required().action((x, c) => c.copy(eventPath = x)).text("Path to json file with events to modify components.")

    }

    def apply(args: Array[String]): Config = {
      parser.parse(args, new Config()) match {
        case Some(cmdOpts) => cmdOpts
        case None => throw new IllegalArgumentException
      }
    }

  }

