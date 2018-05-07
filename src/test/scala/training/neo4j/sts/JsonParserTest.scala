package training.neo4j.sts

import org.scalatest.FunSuite

import scala.io.Source

class JsonParserTest extends FunSuite {

  test("parse initial file") {
println(JsonParser.readInitFile("src/test/ressources/initial.json"))
  }

  test("extract component") {
    val jvalue = JsonParser.readInitFile("src/test/ressources/initial.json")

    println(JsonParser.createComponent(jvalue))


  }


}
