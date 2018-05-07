package training.neo4j.sts

case class Component(id: String,
                     own_state: String,
                     derived_state: String,
                     check_states: Map[String,String],
                     depends_on: List[String],
                     dependency_of: List[String])


