package training.neo4j.sts

import java.io.FileInputStream

import org.json4s.{DefaultFormats, Formats, JObject, JValue}
import org.json4s.jackson.JsonMethods.parse

object JsonParser{

  implicit val formats = DefaultFormats

  def readInitFile(file: String): List[JValue] = {
    val stream = new FileInputStream(file)
    parse(stream).children.head.children.head.children
  }

  def readEventFile(file: String): List[JValue] = {
    val stream = new FileInputStream(file)
    parse(stream).children.head.children
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
