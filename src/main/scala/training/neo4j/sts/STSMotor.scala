package training.neo4j.sts

import com.sun.xml.internal.bind.v2.TODO
import org.neo4j.driver.v1._

import scala.collection.JavaConverters._

case class STSMotor(
                         uri: String,
                        user: String,
                        password: String) {

  val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))

  val session: Session = driver.session()

  val sortedStateList: List[String] = List("alert","warning","clear","no_data")

  def runCypherQuery(query:String) : List[Record] = {

   session.run(query).list().asScala.toList

  }

  //Initialize Graph
  def init(components: List[Component]): Unit ={

    //Delete nodes, relations and constraints
    List("Component","Check").foreach(n=>{
      session.writeTransaction(tx=>tx.run(Neo4jQueries.deleteNode(n)))
      session.writeTransaction(tx=>tx.run(Neo4jQueries.deleteConstraints(n,"id")))}
    )

    //Write constraints
    session.writeTransaction(tx=>tx.run(Neo4jQueries.createConstraint("Component","id")))
    session.writeTransaction(tx=>tx.run(Neo4jQueries.createConstraint("Check","id")))

    components.foreach(c=> {
      //Write components
      session.writeTransaction(tx =>{tx.run(Neo4jQueries.writeComponent(c))}
      )

      //Write dependencies
      //Dependencies are represented as a list of components inside the current component
      c.depends_on.foreach(d =>{
        session.writeTransaction(tx=>tx.run(Neo4jQueries.writeRelation(c.id, d,"Component","Component","DEPENDS_ON")))

      } )

      //Write Check_states
      //1-Create Check labels on nodes
      //2-Create State relations
      c.check_states.foreach(cs => {
        session.writeTransaction(tx => tx.run(Neo4jQueries.writeChecks(cs._1)))
        session.writeTransaction(tx => tx.run(Neo4jQueries.createState(c, cs._1, cs._2)))
      })


    })
  }

  def update(events: List[Event],components: List[Component]) ={

    //For each event
    //First step - delete existing events relations between component and check
    //Second step - create new events relations between component and check
    events.foreach(e=>{
      session.writeTransaction(tx => tx.run(Neo4jQueries.deleteState(e)))
      session.writeTransaction(tx => tx.run(Neo4jQueries.writeRelation(e.check_state,e.component,"Check","Component", e.state)))
    })

    //For each component
    //First step - calculate the new state
    //Second step - Update the own state with the new state
    components.foreach(c=>{
      val newState = worstState(sortedStateList,c)
      session.writeTransaction(tx => tx.run(Neo4jQueries.UpdateOwnState(c,newState)))
    })

  }

  //Find the worst state in the Checks
  def worstState(stateList: List[String],component: Component): String ={
    if(stateList.isEmpty) component.own_state
    else {
      if(runCypherQuery(s"MATCH (c:Component)-[:${stateList.head}]-(ch:Check)" +
        s"WHERE c.id='${component.id}'" +
        s"RETURN c.own_state").isEmpty == false)
        stateList.head
      else worstState(stateList.tail,component)
    }
  }

}

