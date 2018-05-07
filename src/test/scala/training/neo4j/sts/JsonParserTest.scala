package training.neo4j.sts

import org.scalatest.FunSuite

import scala.io.Source

class JsonParserTest extends FunSuite {

  test("parse initial file") {
println(JsonParser.readFile("src/test/ressources/initial.json"))
  }

  test("extract component") {
    val jvalue = JsonParser.readFile("src/test/ressources/initial.json")

    println(JsonParser.createComponent(jvalue))


  }


}
