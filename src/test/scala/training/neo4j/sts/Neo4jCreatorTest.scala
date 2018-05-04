package training.neo4j.sts

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}
import org.scalatest.FunSuite
import scala.collection.JavaConverters._



class Neo4jCreatorTest extends FunSuite {

//  test("write an empty query") {
//
//    val uri: String = "bolt://localhost:7687"
//    val user: String = "neo4j"
//    val password:String = "231287"
//
//    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
//
//    val expected: Boolean = true
//    val actual: Boolean = neo4jCreator.runCypherQuery("").list().isEmpty
//    assert(actual == expected)
//
//  }


  test("send a simple match query") {

    val uri: String = "bolt://localhost:7687"
    val user: String = "neo4j"
    val password:String = "231287"

    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)

    val expected: Int = 2
    val actual: Int = neo4jCreator.runCypherQuery("MATCH (n) RETURN n").length
    assert(actual == expected)

    println(neo4jCreator.runCypherQuery("MATCH (n) RETURN n"))

  }

  test("read a query file") {

    val uri: String = "bolt://localhost:7687"
    val user: String = "neo4j"
    val password:String = "231287"
    val file="queries.txt"

    //Create neo4j session
    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    val queries = CypherQueries().createListOfQueries(file)

   val expected: Int = 2

   val actual: Int = neo4jCreator.runCypherQueries(queries).length //TODO

    assert(actual == expected)

  }

}
