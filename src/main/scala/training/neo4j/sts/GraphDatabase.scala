package training.neo4j.sts

import org.neo4j.driver.v1._

case class STSGraphDatabase(uri) extends GraphDatabase {

  def apply(
             uri:String,
             user:String,
             password:String
           ): GraphDatabase = {

    val driver = GraphDatabase.driver(uri,AuthTokens.basic(user,password))

    logger.debug("load-gigya-data-into-hive, SourceToDataFrame, creating case class SourceToDataFrame.")
    SourceToDataFrame(df = df)
  }



  val driver = GraphDatabase()
}
