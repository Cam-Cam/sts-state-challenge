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
//


  test("Test if the constraint is correctly created in DB"){
    val uri: String = "bolt://localhost:7687"
    val user: String = "neo4j"
    val password:String = "231287"

    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    val jValue = JsonParser.readFile("src/test/ressources/initial.json")
    val components = JsonParser.createComponent(jValue)

    neo4jCreator.createConstraint()

    val expected: Int = 1
    val actual:Int = neo4jCreator.runCypherQuery("CALL db.constraints").length

    assert(actual == expected)

  }

  test("In case the initial.json has at list 1 component, ensure that the database is not null"){

    val uri: String = "bolt://localhost:7687"
    val user: String = "neo4j"
    val password:String = "231287"

    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    val jValue = JsonParser.readFile("src/test/ressources/initial.json")
    val components = JsonParser.createComponent(jValue)

    neo4jCreator.writeComponent(components)

    val expected =  if (!components.isEmpty) 1 else 0

    val actual =  if (!components.isEmpty) neo4jCreator.runCypherQuery("MATCH(n) RETURN n").length else 0

    assert(actual >= expected)


  }

  test("Ensure that relations are correctly written at least for the first component"){

    val uri: String = "bolt://localhost:7687"
    val user: String = "neo4j"
    val password:String = "231287"

    //Create a neo4jCreator object
    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    //Create a list of components from the initial.json file
    val components: List[Component] = JsonParser.createComponent(JsonParser.readFile("src/test/ressources/initial.json"))

    //Get the id and the number of dependencies of the first component
    val id_first_component = components.head.id
    val expected = components.head.depends_on.length

    //Write the dependencies for all components
    neo4jCreator.writeDependencyRelation(components)

    //Get the number of dependencies written in the database for the forst component
    val actual =
      neo4jCreator.
        runCypherQuery(s"MATCH (c1:Component)-[r:DEPENDS_ON]->(c2:Component)" +
                        s" WHERE c1.id='${id_first_component}'" +
                        s"RETURN r").length


    assert(actual == expected)

  }

}
