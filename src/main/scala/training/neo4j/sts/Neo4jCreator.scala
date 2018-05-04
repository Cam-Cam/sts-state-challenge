package training.neo4j.sts

import com.sun.xml.internal.bind.v2.TODO
import org.neo4j.driver.v1._

import scala.collection.JavaConverters._

case class Neo4jCreator(
                         uri: String,
                        user: String,
                        password: String) extends GraphDatabase{

  def runCypherQuery(query:String) : List[Record] = {

    //TODO Handle the case where the query is null
    val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
    val transaction: Transaction = driver.session().beginTransaction()

    transaction.run(query).list().asScala.toList
  }

  def runCypherQueries(queries: List[String]) : List[List[Record]] = {

    //TODO Create a type for queries
    queries.map(q=>runCypherQuery(q))

  }



}
