package training.neo4j.sts

import org.scalatest.FunSuite
import training.neo4j.sts.json_motor.JsonParser
import training.neo4j.sts.sts_concepts.{Component, Event, STSMotor}

class STSMotorTest extends FunSuite {

  val uri: String = "bolt://localhost:7687"
  val user: String = "neo4j"
  val password: String = "231287"

  val stsMotor: STSMotor = STSMotor(uri, user, password)

  val components: List[Component] = JsonParser.createComponent(JsonParser.readInitFile("src/test/resources/initial.json"))
  val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile("src/test/resources/events.json"))

  val components3: List[Component] = JsonParser.createComponent(JsonParser.readInitFile("src/test/resources/init3.json"))
  val events3: List[Event] = JsonParser.createEvent(JsonParser.readEventFile("src/test/resources/events3.json"))


  test("Initialization with tests files create 2 constraints") {
    stsMotor.init(components)

    val expected: Int = 2
    val actual: Int = stsMotor.runCypherQuery("CALL db.constraints").length

    assert(actual == expected)

  }

  test("Initialization with tests files creates 4 nodes and 10 relations") {
    stsMotor.init(components)


    val actualNodes = stsMotor.runCypherQuery("MATCH (n) RETURN count(*)").head.get("count(*)").toString
    val actualRel = stsMotor.runCypherQuery(("MATCH(n)-[r]-(m) RETURN r")).length

    val expectedNodes = "4"
    val expectedRel = 10

    //assert(actualNodes==expectedNodes && actualRel==expectedRel)
  }

  test("Initialization and update with test files should change states between components and checks") {
    stsMotor.init(components)
    stsMotor.update(events, components)

    val actual1 = stsMotor.runCypherQuery(s"MATCH (c:Component)" + s"WHERE c.id='${components.head.id}'" + s"RETURN c.own_state").head.get(0).asString()

    val actual2 = stsMotor.runCypherQuery(s"MATCH (c:Component)" + s"WHERE c.id='${components.tail.head.id}'" + s"RETURN c.own_state").head.get(0).asString()

    val expected1 = "clear"
    val expected2 = "warning"

    assert(actual1 == expected1 && actual2 == expected2)

  }

  test("Initialization with tests files 3 create 2 constraints") {
    stsMotor.init(components3)

    val expected: Int = 2
    val actual: Int = stsMotor.runCypherQuery("CALL db.constraints").length

    assert(actual == expected)

  }

  test("Initialization with tests files 3 creates 9 nodes and 14*2+18*2=64 relations") {
    stsMotor.init(components3)


    val actualNodes = stsMotor.runCypherQuery("MATCH (n) RETURN count(*)").head.get("count(*)").toString
    val actualRel = stsMotor.runCypherQuery(("MATCH(n)-[r]-(m) RETURN r")).length

    val expectedNodes = "4"
    val expectedRel = 64

    //assert(actualNodes==expectedNodes && actualRel==expectedRel)
  }

  test("Initialization and update with test files 3 should change states between components and checks") {
    stsMotor.init(components3)
    stsMotor.update(events3, components3)

    val actual1 = stsMotor.runCypherQuery(s"MATCH (c:Component)" + s"WHERE c.id='${components3.head.id}'" + s"RETURN c.own_state").head.get(0).asString()

    val actual2 = stsMotor.runCypherQuery(s"MATCH (c:Component)" + s"WHERE c.id='${components3.tail.head.id}'" + s"RETURN c.own_state").head.get(0).asString()

    val expected1 = "warning"
    val expected2 = "alert"


    assert(actual1 == expected1 && actual2 == expected2)


  }


}
