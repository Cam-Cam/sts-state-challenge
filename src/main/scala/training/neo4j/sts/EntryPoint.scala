package training.neo4j.sts

object EntryPoint extends App{

  override def main(args: Array[String]): Unit = {

    val components: List[Component] = JsonParser.createComponent(JsonParser.readInitFile(args(0)))
    val events: List[Event] = JsonParser.createEvent(JsonParser.readEventFile(args(1)))

    val uri: String = "bolt://localhost:7687"
    val user: String = "neo4j"
    val password: String = "231287"

    val stsMotor: STSMotor = STSMotor(uri, user, password)
    stsMotor.init(components)
  }


}






