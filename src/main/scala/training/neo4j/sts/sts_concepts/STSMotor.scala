package training.neo4j.sts.sts_concepts

import org.neo4j.driver.v1._
import training.neo4j.sts.neo4j_motor.Neo4jQueries


import scala.collection.JavaConverters._

case class STSMotor(
                         uri: String,
                        user: String,
                        password: String) {

  val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))

  //val tx: Transaction = session.beginTransaction()

  val sortedStateList: List[String] = List("alert","warning","clear","no_data")

  def runCypherQuery(query:String) : List[Record] = {

    val session: Session = driver.session()
   session.run(query).list().asScala.toList


  }

  //Initialize Graph
  def init(components: List[Component]): Unit = {

    val schemaUpdateSession: Session = driver.session()

    //Delete all constraints
    schemaUpdateSession.writeTransaction(tx =>{
      tx.run(Neo4jQueries.deleteAllConstraints())
    //Write constraints
    tx.run(Neo4jQueries.createConstraint("Component", "id"))
    tx.run(Neo4jQueries.createConstraint("Check", "id"))
    })

    schemaUpdateSession.close()

    val dataUpdateSession: Session = driver.session()

    dataUpdateSession.writeTransaction(tx => {
      List("Component", "Check").foreach(n => {
        //For each component
        components.foreach(c => {
          //Write components
          tx.run(Neo4jQueries.writeComponent(c))

          //Write dependencies
          //Dependencies are represented as a list of components inside the current component
          c.depends_on.foreach(d => {
            tx.run(Neo4jQueries.writeRelation("Component",c.id,"Component", d, "DEPENDS_ON"))
          })

          //Write Check_states
          //1-Create Check labels on nodes
          //2-Create State relations
          c.check_states.foreach(cs => {
            tx.run(Neo4jQueries.writeCheck(cs._1))
            tx.run(Neo4jQueries.writeRelation("Component",c.id,"Check",cs._1,cs._2))

          })
        })
      })
    })

    dataUpdateSession.close()

  }

  def update(events: List[Event],components: List[Component]) ={

    val sessionToUpdateRelations: Session = driver.session()
    val sessionToUpdateOwnState: Session = driver.session()
    val sessionToUpdateData:Session = driver.session()

    sessionToUpdateRelations.writeTransaction(tx => {
      //For each event
      //First step - delete existing events relations between component and check
      //Second step - create new events relations between component and check
      events.foreach(e => {
        tx.run(Neo4jQueries.deleteStateRelation(e))
        tx.run(Neo4jQueries.writeRelation("Check", e.check_state, "Component", e.component, e.state))
      })
    })

    sessionToUpdateRelations.close()

    sessionToUpdateData.writeTransaction(tx => {
      //For each component
      //First step - calculate the new state
      //Second step - Update the own state with the new state
      components.foreach(c=>{
        val newState = worstState(sortedStateList,c)
        tx.run(Neo4jQueries.UpdateOwnState(c,newState))
      })
    })

    sessionToUpdateData.close()

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

