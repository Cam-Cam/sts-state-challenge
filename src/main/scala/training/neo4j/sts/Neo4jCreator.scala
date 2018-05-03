package training.neo4j.sts

import org.neo4j.driver.v1._

case class Neo4jCreator(
                         uri: String,
                        user: String,
                        password: String) extends GraphDatabase{

  def writeQuery(query:String) = {

    val driver = GraphDatabase.driver(uri,AuthTokens.basic(user,password))

    val transaction = driver.session().beginTransaction()

    transaction.run(query)

  }







}
