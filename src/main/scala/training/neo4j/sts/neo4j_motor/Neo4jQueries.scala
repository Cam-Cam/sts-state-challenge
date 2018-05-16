package training.neo4j.sts.neo4j_motor

import training.neo4j.sts.sts_concepts.{Component, Event}

object Neo4jQueries {

  def  constraintLongQuery() = {

    s"CALL apoc.util.sleep(20000)" // wait for 20 seconds

  }

  def createConstraint(node: String, key: String): String = {
    s"CREATE CONSTRAINT ON (n:${node}) ASSERT n.${key} IS UNIQUE"
  }

  def writeComponent(component: Component): String = {
    //Thread.sleep(30000)
    s"MERGE (c:Component {id:'${component.id}'})" +
      s"ON CREATE SET c.own_state= '${component.own_state}', c.derived_state='${component.derived_state}', c.depends_on='${component.depends_on}', c.dependency_of='${component.dependency_of}'" +
    s"ON MATCH SET c.own_state= '${component.own_state}', c.derived_state='${component.derived_state}', c.depends_on='${component.depends_on}', c.dependency_of='${component.dependency_of}'"
  }

  def writeCheck(check_id: String): String = {
    s"MERGE(:Check {id:'${check_id}'})"
  }

  def deleteStateRelation(event: Event) = {
    s"MATCH (c1:Component)-[r]-(ch1:Check) WHERE c1.id='${event.component}' AND ch1.id='${event.check_state}' DELETE r"
  }

  def writeRelation( label1: String,id1: String, label2:String,id2: String, relation: String): String = {
    s"MERGE(n1:${label1}{id:'${id1}'}) MERGE(n2:${label2}{id:'${id2}'}) MERGE(n1)-[:${relation}]-(n2)"
  }

  def UpdateOwnState(component: Component,newState: String): String = {
    s"MATCH (c:Component)" + s"WHERE c.id='${component.id}'" + s"SET c.own_state = '${newState}'"
  }

  def deleteAllConstraints()= "CALL apoc.schema.assert({},{})"


}
