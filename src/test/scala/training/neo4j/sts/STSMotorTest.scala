package training.neo4j.sts

import org.scalatest.FunSuite

class STSMotorTest extends FunSuite {

  val uri: String = "bolt://localhost:7687"
  val user: String = "neo4j"
  val password:String = "231287"

  val stsMotor: STSMotor = STSMotor(uri,user,password)

  val components: List[Component] = JsonParser.createComponent(JsonParser.readInitFile("src/test/ressources/initial.json"))
  val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile("src/test/ressources/events.json"))


  test("Initialization with tests files create 2 constraints"){

    stsMotor.init(components)

    val expected: Int = 2
    val actual:Int = stsMotor.runCypherQuery("CALL db.constraints").length

    assert(actual == expected)

  }

  test("Initialization with tests files creates 4 nodes and 10 relations"){

    stsMotor.init(components)


    val actualNodes = stsMotor.runCypherQuery("MATCH (n) RETURN count(*)").head.get("count(*)").toString
    val actualRel = stsMotor.runCypherQuery(("MATCH(n)-[r]-(m) RETURN r")).length

    val expectedNodes = "4"
    val expectedRel = 10

    //assert(actualNodes==expectedNodes && actualRel==expectedRel)
  }

  test("Initialization and update with test files should change states between components and checks"){

    stsMotor.init(components)
        stsMotor.update(events,components)

    val actual1= stsMotor.runCypherQuery(
      s"MATCH (c:Component)" +
        s"WHERE c.id='${components.head.id}'" +
        s"RETURN c.own_state").head.get(0).asString()

    val actual2= stsMotor.runCypherQuery(
      s"MATCH (c:Component)" +
        s"WHERE c.id='${components.tail.head.id}'" +
        s"RETURN c.own_state").head.get(0).asString()

        val expected1="clear"
        val expected2="warning"



        assert(actual1==expected1 && actual2==expected2)


      }








}
