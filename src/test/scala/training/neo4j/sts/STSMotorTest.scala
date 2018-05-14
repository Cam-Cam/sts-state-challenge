package training.neo4j.sts

import org.neo4j.driver.v1._
import org.scalatest.FunSuite




class STSMotorTest extends FunSuite {

  val uri: String = "bolt://localhost:7687"
  val user: String = "neo4j"
  val password:String = "231287"

  val stsMotor: STSMotor = STSMotor(uri,user,password)

  val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))

  val session: Session = driver.session()

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

    assert(actualNodes==expectedNodes && actualRel==expectedRel)
  }

  test("Initialization and update with test files should change states between components and checks"){
        stsMotor.init(components)
        stsMotor.update(events,components)

        val expected1="clear"
        val expected2="warning"


        val actual1= stsMotor.runCypherQuery(
          s"MATCH (c:Component)" +
            s"WHERE c.id='${components.head.id}'" +
            s"RETURN c.own_state").head.get(0).asString()

        val actual2= stsMotor.runCypherQuery(
          s"MATCH (c:Component)" +
            s"WHERE c.id='${components.tail.head.id}'" +
            s"RETURN c.own_state").head.get(0).asString()

        assert(actual1==expected1 && actual2==expected2)


      }

  test("Infinite function"){

    val tx: Transaction = session.beginTransaction()

      tx.run(Neo4jQueries.constraintErrorQuery())
      tx.success()





  }




//  test("In case the initial.json has at list 1 component, ensure that the database is not null"){
//
//    stsMotor.writeComponent(components)
//
//    val expected =  if (!components.isEmpty) 1 else 0
//
//    val actual =  if (!components.isEmpty) stsMotor.runCypherQuery("MATCH(n) RETURN n").length else 0
//
//    assert(actual >= expected)
//
//
//  }
//
//  test("Ensure that relations are correctly written at least for the first component"){
//
//    //Get the id and the number of dependencies of the first component
//    val id_first_component = components.head.id
//    val expected = components.head.depends_on.length
//
//    //Write the dependencies for all components
//    stsMotor.writeDependencyRelation(components)
//
//    //Get the number of dependencies written in the database for the forst component
//    val actual =
//      stsMotor.
//        runCypherQuery(s"MATCH (c1:Component)-[r:DEPENDS_ON]->(c2:Component)" +
//                        s" WHERE c1.id='${id_first_component}'" +
//                        s"RETURN r").length
//
//
//    assert(actual == expected)
//
//  }
//
//  test("Ensure that checks are created"){
//
//    //Get the id and the number of checks of the first component
//    val id_first_component = components.head.id
//    val expected = components.head.check_states.size
//
//    //Write the dependencies for all components
//    stsMotor.writeChecks(components)
//
//    //Get the number of dependencies written in the database for the forst component
//    val actual =
//      stsMotor.
//        runCypherQuery(s"MATCH (check:Check) RETURN check").length
//
//    assert(actual == expected)
//
//  }
//
//  test("Ensure that relation between components and checks are created"){
//
//    //Get the id of the first component
//    val id_first_component = components.head.id
//    //Get the first check of the first component
//    val id_first_check = components.head.check_states.head._1
//    val state_first_check = components.head.check_states.head._2
//
//    val expected = components.head.check_states.size
//
//    //Write the dependencies for all components
//    stsMotor.writeStates(components)
//
//    //Get the number of dependencies written in the database for the forst component
//    val actual =stsMotor.runCypherQuery(
//      s"MATCH (c1:Component)-[r:${state_first_check}]-(ch1:Check)" +
//        s" WHERE c1.id='${id_first_component}'" +
//        s"RETURN ch1,r").length
//
//    assert(expected==actual)
//
//  }
//
//  //UPDATE TEST
//
//  test("Ensure that new relation is created") {
//
//    //get component and check of the first event
//    val firstComp = events.head.component
//    val firstCheck = events.head.check_state
//
//    val expected: Int = stsMotor.runCypherQuery(
//      "MATCH (c1:Component)-[r]-(ch1:Check)" +
//        s" WHERE c1.id='${firstComp}' AND ch1.id='${firstCheck}'" +
//        s"RETURN r").length
//
//    stsMotor.updateCheckState(events)
//
//    val actual = stsMotor.runCypherQuery(
//      "MATCH (c1:Component)-[r]-(ch1:Check)" +
//        s" WHERE c1.id='${firstComp}' AND ch1.id='${firstCheck}'" +
//        s"RETURN r").length
//
//    assert(actual==expected)
//  }
//
//  test("Update own state state") {
//
//    val expected = components.head.own_state.toString
//
//    stsMotor.updateOwnState(components)
//
//    val actual = stsMotor.runCypherQuery(
//      s"MATCH (c:Component)" +
//      s"WHERE c.id='${components.head.id}'" +
//        s"RETURN c.own_state").head.get(0).asString()
//
//  assert(actual!=expected)
//
//  }
//
//
//

//




}
