package training.neo4j.sts

object Neo4jQueries {

  def createConstraint(node: String, key: String): String = {
    s"CREATE CONSTRAINT ON (n:${node}) ASSERT n.${key} IS UNIQUE "
  }

  def writeComponent(component: Component): String = {
    s"MERGE (:Component {id:'${component.id}',own_state: '${component.own_state}',derived_state:'${component.derived_state}',depends_on:'${component.depends_on}',dependency_of:'${component.dependency_of}'})"
  }

  def writeChecks(check_id: String): String = {
    s"MERGE(:Check {id:'${check_id}'})"
  }

  def deleteState(event: Event) = {
    s"MATCH (c1:Component)-[r]-(ch1:Check) WHERE c1.id='${event.component}' AND ch1.id='${event.check_state}' DELETE r"
  }

  def createState(component: Component, check_id: String, check_state: String): String = {
    s"MERGE(check1:Check {id:'${check_id}'}) MERGE(c1:Component{id:'${component.id}'}) MERGE(check1)-[:${check_state}]->(c1)"
  }

  def writeDependencyRelation(id1: String, id2: String): String = {
    s"MERGE(c1:Component{id:'${id1}'}) MERGE(c2:Component{id:'${id2}'}) MERGE(c1)-[:DEPENDS_ON]->(c2)"
  }

  def deleteNode(node: String) ={
    "MATCH (n) DETACH DELETE n"
  }

}
