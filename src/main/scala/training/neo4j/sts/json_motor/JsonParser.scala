package training.neo4j.sts.json_motor

import java.io.FileInputStream

import org.json4s.jackson.JsonMethods.parse
import org.json4s.{DefaultFormats, JValue}
import training.neo4j.sts.sts_concepts.{Component, Event}

object JsonParser{

  implicit val formats = DefaultFormats

  def readInitFile(file: String): List[JValue] = {
    val stream = new FileInputStream(file)
    val jvalue: List[JValue] = parse(stream).children.head.children.head.children
    stream.close()
    jvalue
  }

  def readEventFile(file: String): List[JValue] = {
    val stream = new FileInputStream(file)
    val jvalue: List[JValue] = parse(stream).children.head.children
    stream.close()
    jvalue
  }

  def createComponent(json: List[JValue]): List[Component] = {

    if(json.isEmpty) List()
    else List(json.head.extract[Component]) ::: createComponent(json.tail)
  }

  def createEvent(json: List[JValue]): List[Event] = {

    if(json.isEmpty) List()
    else List(json.head.extract[Event]) ::: createEvent(json.tail)
  }

}
