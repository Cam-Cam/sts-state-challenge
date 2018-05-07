package training.neo4j.sts

import com.sun.xml.internal.bind.v2.TODO
import org.neo4j.driver.v1._

import scala.collection.JavaConverters._

case class STSMotor(
                         uri: String,
                        user: String,
                        password: String) extends GraphDatabase{

  val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
  val session: Session = driver.session()

  def runCypherQuery(query:String) : List[Record] = {

   session.run(query).list().asScala.toList

  }


  //Create constraint on the component.id and check.id
  def createConstraints():Unit = {

    session.run(Neo4jQueries.createConstraint("Component","id"))
    session.run(Neo4jQueries.createConstraint("Check","id"))

  }

  //Create component from init file
  def writeComponent(components: List[Component]): Unit = {

    components.foreach(c=>
      session.run(Neo4jQueries.writeComponent(c)))
  }

  //Create dependencies between Components
  def writeDependencyRelation(components: List[Component]) = {

    components.foreach(c=>
      c.depends_on.foreach(d=>
        session.run(Neo4jQueries.writeDependencyRelation(c.id,d)
        )
      )
    )
  }

  //Create checks from init file
  def writeChecks(components: List[Component]) = {

    components.foreach(c=>
      c.check_states.foreach(cs=>
        Neo4jQueries.writeChecks(cs._1))
    )

  }

  //Create the state relations between Components and Checks
  def writeStates(components: List[Component]) = {

    components.foreach(c=>
      c.check_states.foreach(cs=>
        session.run(Neo4jQueries.createState(c,cs._1,cs._2)
        )
      )
    )
  }

  //Initialize Graph
  def init(components: List[Component]) ={
    //delete everything
    session.run(Neo4jQueries.deleteNode("Component"))
    session.run(Neo4jQueries.deleteNode("Check"))
    createConstraints()
    writeComponent(components)
    writeDependencyRelation(components)
    writeChecks(components)
    writeStates(components)
  }


  //First step - delete existing events relations between component and check
  def deleteState(events: List[Event]) = {
    events.foreach(e =>
      session.run(Neo4jQueries.deleteState(e))
    )
  }

  //Second step - create new events relations between component and check
  def updateState(events: List[Event]) = {

    events.foreach(e =>
      session.run(
        s"MERGE(check1:Check {id:'${e.check_state}'}) " +
          s"MERGE(c1:Component{id:'${e.component}'}) " +
          s"MERGE(check1)-[:${e.state}]->(c1)"))
  }

  //Third step - update own_state

  //Fourth step - update dependencies




}

