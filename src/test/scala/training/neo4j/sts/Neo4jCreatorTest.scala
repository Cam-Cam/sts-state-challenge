package training.neo4j.sts

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}
import org.scalatest.FunSuite
import scala.collection.JavaConverters._



class Neo4jCreatorTest extends FunSuite {

  val uri: String = "bolt://localhost:7687"
  val user: String = "neo4j"
  val password:String = "231287"

  test("Test if the constraint is correctly created in DB"){

    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    val jValue = JsonParser.readFile("src/test/ressources/initial.json")
    val components = JsonParser.createComponent(jValue)

    neo4jCreator.createConstraints()

    val expected: Int = 2
    val actual:Int = neo4jCreator.runCypherQuery("CALL db.constraints").length

    assert(actual == expected)

  }

  test("In case the initial.json has at list 1 component, ensure that the database is not null"){

    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    val jValue = JsonParser.readFile("src/test/ressources/initial.json")
    val components = JsonParser.createComponent(jValue)

    neo4jCreator.writeComponent(components)

    val expected =  if (!components.isEmpty) 1 else 0

    val actual =  if (!components.isEmpty) neo4jCreator.runCypherQuery("MATCH(n) RETURN n").length else 0

    assert(actual >= expected)


  }

  test("Ensure that relations are correctly written at least for the first component"){

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

  test("Ensure that checks are created"){

    //Create a neo4jCreator object
    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    //Create a list of components from the initial.json file
    val components: List[Component] = JsonParser.createComponent(JsonParser.readFile("src/test/ressources/initial.json"))

    //Get the id and the number of checks of the first component
    val id_first_component = components.head.id
    val expected = components.head.check_states.size

    //Write the dependencies for all components
    neo4jCreator.writeChecks(components)

    //Get the number of dependencies written in the database for the forst component
    val actual =
      neo4jCreator.
        runCypherQuery(s"MATCH (check:Check) RETURN check").length

    assert(actual == expected)

  }

  test("Ensure that relation between components and checks are created"){

    //Create a neo4jCreator object
    val neo4jCreator: Neo4jCreator = Neo4jCreator(uri,user,password)
    //Create a list of components from the initial.json file
    val components: List[Component] = JsonParser.createComponent(JsonParser.readFile("src/test/ressources/initial.json"))

    //Get the id of the first component
    val id_first_component = components.head.id
    //Get the first check of the first component
    val id_first_check = components.head.check_states.head._1
    val state_first_check = components.head.check_states.head._2

    val expected = components.head.check_states.size

    //Write the dependencies for all components
    neo4jCreator.writeStates(components)

    //Get the number of dependencies written in the database for the forst component
    val actual =neo4jCreator.runCypherQuery(
      s"MATCH (c1:Component)-[r:${state_first_check}]-(ch1:Check)" +
        s" WHERE c1.id='${id_first_component}'" +
        s"RETURN ch1,r").length

    assert(expected==actual)

  }

}
