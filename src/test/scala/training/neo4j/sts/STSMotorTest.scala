package training.neo4j.sts

import org.neo4j.driver.v1.{AuthTokens, GraphDatabase}
import org.scalatest.FunSuite
import scala.collection.JavaConverters._



class STSMotorTest extends FunSuite {

  val uri: String = "bolt://localhost:7687"
  val user: String = "neo4j"
  val password:String = "231287"

  val stsMotor: STSMotor = STSMotor(uri,user,password)
  val jValue = JsonParser.readInitFile("src/test/ressources/initial.json")
  val components: List[Component] = JsonParser.createComponent(jValue)
  val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile("src/test/ressources/events.json"))

  //INITIALIZATION TESTS

  test("Initialization with tests files creates 4 nodes and 10 relations"){

    stsMotor.init(components)


    val actualNodes = stsMotor.runCypherQuery("MATCH (n) RETURN count(*)").head.get("count(*)").toString
    val actualRel = stsMotor.runCypherQuery(("MATCH(n)-[r]-(m) RETURN r")).length

    val expectedNodes = "4"
    val expectedRel = 10

    assert(actualNodes==expectedNodes && actualRel==expectedRel)
  }

  test("Test if the constraint is correctly created in DB"){

    stsMotor.createConstraints()

    val expected: Int = 2
    val actual:Int = stsMotor.runCypherQuery("CALL db.constraints").length

    assert(actual == expected)

  }

  test("In case the initial.json has at list 1 component, ensure that the database is not null"){

    stsMotor.writeComponent(components)

    val expected =  if (!components.isEmpty) 1 else 0

    val actual =  if (!components.isEmpty) stsMotor.runCypherQuery("MATCH(n) RETURN n").length else 0

    assert(actual >= expected)


  }

  test("Ensure that relations are correctly written at least for the first component"){

    //Get the id and the number of dependencies of the first component
    val id_first_component = components.head.id
    val expected = components.head.depends_on.length

    //Write the dependencies for all components
    stsMotor.writeDependencyRelation(components)

    //Get the number of dependencies written in the database for the forst component
    val actual =
      stsMotor.
        runCypherQuery(s"MATCH (c1:Component)-[r:DEPENDS_ON]->(c2:Component)" +
                        s" WHERE c1.id='${id_first_component}'" +
                        s"RETURN r").length


    assert(actual == expected)

  }

  test("Ensure that checks are created"){

    //Get the id and the number of checks of the first component
    val id_first_component = components.head.id
    val expected = components.head.check_states.size

    //Write the dependencies for all components
    stsMotor.writeChecks(components)

    //Get the number of dependencies written in the database for the forst component
    val actual =
      stsMotor.
        runCypherQuery(s"MATCH (check:Check) RETURN check").length

    assert(actual == expected)

  }

  test("Ensure that relation between components and checks are created"){

    //Get the id of the first component
    val id_first_component = components.head.id
    //Get the first check of the first component
    val id_first_check = components.head.check_states.head._1
    val state_first_check = components.head.check_states.head._2

    val expected = components.head.check_states.size

    //Write the dependencies for all components
    stsMotor.writeStates(components)

    //Get the number of dependencies written in the database for the forst component
    val actual =stsMotor.runCypherQuery(
      s"MATCH (c1:Component)-[r:${state_first_check}]-(ch1:Check)" +
        s" WHERE c1.id='${id_first_component}'" +
        s"RETURN ch1,r").length

    assert(expected==actual)

  }

  //UPDATE TEST

  test("Ensure that relation is deleted") {
    //get component and check of the first event
    val firstComp = events.head.component
    val firstCheck = events.head.check_state

    val oldRelations: Int = stsMotor.runCypherQuery("MATCH (c1:Component)-[r]-(ch1:Check)" + s" WHERE c1.id='${firstComp}' AND ch1.id='${firstCheck}'" + s"RETURN r").length

    val nbEvents: Int = events.filter(_.component == firstComp).filter(_.check_state == firstCheck).length

    stsMotor.deleteState(events)

    val actual = stsMotor.runCypherQuery("MATCH (c1:Component)-[r]-(ch1:Check)" + s" WHERE c1.id='${firstComp}' AND ch1.id='${firstCheck}'" + s"RETURN r").length

    val expected = oldRelations - nbEvents

    assert(actual == expected)
  }


  test("Ensure that new relation is created") {

    //get component and check of the first event
    val firstComp = events.head.component
    val firstCheck = events.head.check_state

    val oldRelations: Int = stsMotor.runCypherQuery(
      "MATCH (c1:Component)-[r]-(ch1:Check)" +
        s" WHERE c1.id='${firstComp}' AND ch1.id='${firstCheck}'" +
        s"RETURN r").length

    val nbEvents: Int = events.filter(_.component==firstComp).filter(_.check_state==firstCheck).length

    stsMotor.updateState(events)

    val actual = stsMotor.runCypherQuery(
      "MATCH (c1:Component)-[r]-(ch1:Check)" +
        s" WHERE c1.id='${firstComp}' AND ch1.id='${firstCheck}'" +
        s"RETURN r").length

    val expected = oldRelations+nbEvents

    assert(actual==expected)


  }




}
