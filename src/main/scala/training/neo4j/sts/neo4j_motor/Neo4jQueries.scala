package training.neo4j.sts.neo4j_motor


import org.slf4j.LoggerFactory
import training.neo4j.sts.sts_concepts.{Component, Event}

class Neo4jQueries

object Neo4jQueries  {

  val logger = LoggerFactory.getLogger(classOf[Neo4jQueries])

  logger.info(s"Run query constraintLong")
  def  constraintLongQuery() = {
    s"CALL apoc.util.sleep(20000)" // wait for 20 seconds
  }

  def createConstraint(node: String, key: String): String = {
    logger.info(s"Run create constraint query on ${node} with parameter n.${key}")
    s"CREATE CONSTRAINT ON (n:${node}) ASSERT n.${key} IS UNIQUE"
  }

  def writeComponent(component: Component): String = {
    logger.info(s"Merge component ${component.id} with" +
        s"[own state : ${component.own_state}]-" +
        s"[derived_state : ${component.own_state}]-" +
        s"[depends_on : ${component.depends_on}]-" +
        s"[dependency_of : ${component.dependency_of}]")

    s"MERGE (c:Component {id:'${component.id}'})" +
      s"ON CREATE SET c.own_state= '${component.own_state}'," +
      s"c.derived_state='${component.derived_state}'," +
      s"c.depends_on='${component.depends_on}'," +
      s"c.dependency_of='${component.dependency_of}'" +
    s"ON MATCH SET c.own_state= '${component.own_state}'," +
      s"c.derived_state='${component.derived_state}'," +
      s"c.depends_on='${component.depends_on}'," +
      s"c.dependency_of='${component.dependency_of}'"
  }

  def writeCheck(check_id: String): String = {
    logger.info(s"Merge Check ${check_id}")
    s"MERGE(:Check {id:'${check_id}'})"
  }

  def deleteStateRelation(event: Event) = {
    logger.info(s"Delete state relation between ${event.component} and ${event.check_state} ")
    s"MATCH (c1:Component)-[r]-(ch1:Check) WHERE c1.id='${event.component}' AND ch1.id='${event.check_state}' DELETE r"
  }

  def writeRelation( label1: String,id1: String, label2:String,id2: String, relation: String): String = {
    logger.info(s"Create state ${relation} relation between ${id1} and ${id2} ")
    s"MERGE(n1:${label1}{id:'${id1}'}) MERGE(n2:${label2}{id:'${id2}'}) MERGE(n1)-[:${relation}]-(n2)"
  }

  def UpdateOwnState(component: Component,newState: String): String = {
    logger.info(s"Update state of ${component.id} to ${newState}")
    s"MATCH (c:Component)" + s"WHERE c.id='${component.id}'" + s"SET c.own_state = '${newState}'"
  }

  def deleteAllConstraints()= {
    logger.info(s"Delete all constraints")
    "CALL apoc.schema.assert({},{})"
  }


}
