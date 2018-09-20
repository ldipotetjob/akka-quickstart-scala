package com.ldg.utilities

import java.io.File
import com.ldg.utilities.MoviePageTypes.{cronExpresion, moviePage}
import com.typesafe.config.{Config, ConfigFactory}
import com.ldg.utilities.ConfigUtilities._

import scala.collection.JavaConverters._

//TODO change for MAKE ABLE read any configuration. Its possible configuring file of conf and attribute to read
trait ConfigurationBroker {

  val internalConfLoader = ConfigFactory.load()

  /**
    *
    * @return List[(cronexpresssion,cronexpresssion)] if OK
    *         List.empty[(String,Int)] if fail => there is an error in the config file
    */
  def cronExpressionMatchGame: List[(cronExpresion, moviePage)] = {

    /**
      * Read configuration file(cronmatches.conf) that contain cronexpression for every match
      *
      * This file is NOT in the resource file because this is a mutable file that need to be updated
      * when the scheduling change for every week so it can NOT be into a jar file when code be deployed
      * in production. For tah reason we need to parse the config file instead of simply
      * .load(fileconfigname_without_extension)
      *
      *
      * @return
      */

    def configCronmatches: Config = ConfigFactory.parseFile(
      new File(internalConfLoader.getString("akka.pathofschedulerfileconf.schedulerfileconf"))
    )

    //return  Either[FailureTrait,R] where R: List[(cronexpresssion,matchgame)]
    getConfig(/** Read Cron Expression  from configuration file */ConfigFactory.load(configCronmatches)){

      /**
        * We could build the tuple directly but the order of the keys is random,that means that first
        * element could be "cronexpresssion" or "matchgame"
        */

      /**
        *
        * get the MORE recent cron Expression for set scheduling the specific game
        *
        * HashMap[String, String] <=> [cronexpresssion,matchgame]
        *
        * */

      config =>
        val vomoviePageConf = config.getAnyRefList("schedule.moviepagemejorenvo").
          asScala.toList.
          asInstanceOf[List[java.util.HashMap[String, String]]]

        vomoviePageConf.map(x=>(x.get("cronexpresssion"), x.get("moviepage").toInt))
    }
  } match {

    case Right(cronExpressionMatch) => cronExpressionMatch

    case Left(fail) => {
      /**
        * Don't stop the platform so what do you need in this case in this case could be fire
        * another instance of BootScheduled with the NOT-Fired moviepages or re-schedule the same
        * task in next dates in the config file
        */

      log.error( "Error reading cronexpression configuration file: {}", fail.message)
      List.empty[(cronExpresion, moviePage)]
    }
  }

  /**
    * first => group our list for all games that have the same cron expression
    * That mean in cronmatches.conf: schedule {
    *                                  defaultTimezone = "Europe/London"
    *                                  moviepagemejorenvo = [
    *                                    {
    *                                      cronexpresssion = "0 54 12 6 3 ? *"
    *                                      moviepage = "40"
    *                                    }
    *
    * ref. https://docs.scala-lang.org/overviews/collections/maps.html
    *
    * @return List[(cronexpresssion->List[matchgame])]
    */
  def cronExpreesionMatches:List[(cronExpresion,List[moviePage])] = {
    cronExpressionMatchGame.groupBy { case (cronExpression,_) => cronExpression}.
      mapValues(_.map { case (_, matchgame) => matchgame  })
  }.toList
}