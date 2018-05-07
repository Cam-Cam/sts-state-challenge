package training.neo4j.sts

case class Event(timestamp: String,
                 component: String,
                  check_state:String,
                  state: String)
