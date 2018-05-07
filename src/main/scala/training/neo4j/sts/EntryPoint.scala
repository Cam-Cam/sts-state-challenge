package training.neo4j.sts

object EntryPoint extends App {


  val uri: String = "bolt://localhost:7687"
  val user: String = "neo4j"
  val password:String = "231287"

  val stsMotor: STSMotor = STSMotor(uri,user,password)

  //init and event data sources
  val components: List[Component] = JsonParser.createComponent(JsonParser.readInitFile("src/test/ressources/initial.json"))
  val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile("src/test/ressources/events.json"))

  def Main(args: Array[String]): Unit =
  {
    stsMotor.init(components)
  }

}
