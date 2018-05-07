package training.neo4j.sts

import com.sun.xml.internal.bind.v2.TODO
import org.neo4j.driver.v1._

import scala.collection.JavaConverters._

case class Neo4jCreator(
                         uri: String,
                        user: String,
                        password: String) extends GraphDatabase{

  val driver: Driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password))
  val session: Session = driver.session()

  def runCypherQuery(query:String) : List[Record] = {

   session.run(query).list().asScala.toList

  }


  //Create constraint on the node id
  def createConstraint():Unit = {

    session.run(("CREATE CONSTRAINT ON (c:Component) ASSERT c.id IS UNIQUE"))

  }


  def writeComponent(components: List[Component]): Unit = {

    components.foreach(c=>
      session.run(
        s"MERGE (:Component {" +
          s"id:'${c.id}'," +
          s"own_state: '${c.own_state}'," +
          s"derived_state:'${c.derived_state}'," +
          s"depends_on:'${c.depends_on}'," +
          s"dependency_of:'${c.dependency_of}'})"
      ))
  }

  def writeDependencyRelation(components: List[Component]) = {

    components.foreach(c=>
      c.depends_on.foreach(d=>
        session.run(
          s"MERGE(c1:Component{id:'${c.id}'})" +
            s"MERGE(c2:Component{id:'${d}'})" +
            s"MERGE(c1)-[:DEPENDS_ON]->(c2)"
        )
      )
    )

  }




}

