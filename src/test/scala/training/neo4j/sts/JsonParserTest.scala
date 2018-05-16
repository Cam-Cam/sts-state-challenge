package training.neo4j.sts

import org.scalatest.FunSuite
import training.neo4j.sts.jsonMotor.JsonParser

import scala.io.Source

class JsonParserTest extends FunSuite {

  test("parse initial file") {
println(JsonParser.readInitFile("src/test/resources/initial.json"))
  }

  test("extract component") {
    val jvalue = JsonParser.readInitFile("src/test/resources/initial.json")

    println(JsonParser.createComponent(jvalue))


  }


}
