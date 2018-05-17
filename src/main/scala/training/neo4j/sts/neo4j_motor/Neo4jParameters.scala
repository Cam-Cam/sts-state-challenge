package training.neo4j.sts.neo4j_motor

import java.util.Properties

case class Neo4jParameters(uri: String,
                           user: String,
                           password: String)

object Neo4jParameters {
  def apply(): Neo4jParameters = {

    val properties: Properties = new Properties()
    properties.load(this.getClass.getResourceAsStream("/neo4jParameters.properties"))

    val uri: String = properties.getProperty("uri")
    val user: String = properties.getProperty("user")
    val password: String = properties.getProperty("password")

    Neo4jParameters(uri, user, password)
  }
}
