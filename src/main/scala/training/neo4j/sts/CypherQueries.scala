package training.neo4j.sts

import scala.io.Source

case class CypherQueries(){

  def createListOfQueries(file:String): List[String] = {

    Source.fromResource(file).getLines().toList


  }

}



