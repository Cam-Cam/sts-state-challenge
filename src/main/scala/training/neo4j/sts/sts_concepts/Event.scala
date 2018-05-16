package training.neo4j.sts.sts_concepts

case class Event(timestamp: String,
                 component: String,
                  check_state:String,
                  state: String)
