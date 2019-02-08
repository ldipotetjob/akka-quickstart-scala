package com.ldg.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.ldg.actors.IOTask._
import com.ldg.actors.IoTAdmin.{ErrorStopActor, ParseUrlFilms, StopProcess}
import com.ldg.actors.ProcessMoviePage.ProccessPage
import com.ldg.moviepages.VoMoviePages
import com.ldg.utilities.MoviePageTypes.moviePage
import com.ldg.utilities.{ConstantsObject, Result}
import com.typesafe.config.ConfigFactory

object IoTAdmin {
  case class ParseUrlFilms(listTitles: List[moviePage], actorRef: Option[ActorRef]=None)
  case object ErrorStopActor
  case object StopProcess
  def props: Props = Props(new IoTAdmin)
}

//In addition to encapsulating operations, it is important to modularize the tests
trait MessageToTargetActors {

  def sendIOMessage(actorTarget: ActorRef, msg: IOTaskMessages): Unit = actorTarget ! msg

  def sendParserUrlMessage(actorTarget: ActorRef, msg: ProccessPage): Unit = actorTarget ! msg
}

/**This actor Fire the Crawler for all web sites needed to crawl*/

/**
  * LifeCycle of IoTAdmin:
  *
  * 2 - ParseUrlFilms for parse specific URL
  *
  * 3 - Log & Stop process => TODO: make this behaviour for BootDaemonsSheduled too
  *
  * 4 - Terminated(urlMoviePage): When 2- has been done there are two choices:
  *   4.1 - For NOT SCHEDULED process finish all process and stop akka daemons
  *   4.2 - For SCHEDULED process ignore request and keep waiting for other calls
  *
  */

//TODO:See if is better create only ONCE time an actor an NOT every time that be necessary use it
class IoTAdmin extends Actor with ActorLogging with MessageToTargetActors{
  override def preStart(): Unit = log.info("Start process in Akka Scheduler Example")
  override def postStop(): Unit = log.info("Stop process in Akka Schedule Example")
  // TODO checking perfromance: http://docs.scala-lang.org/overviews/collections/performance-characteristics.html

  var watched = Map.empty[ActorRef, Int]

  val conf = ConfigFactory.load()

  val includeconf:String= conf.getString("akka.testinclude")


  override def receive = {

    case ParseUrlFilms(urlPageNunmList, optActorRef) =>

      urlPageNunmList.foreach(
              urlpagenumber => {
                val currentActorRef: ActorRef = context.actorOf(ProcessMoviePage.props)
                watched += currentActorRef -> urlpagenumber
                // watcher for every actor that is created 'cause the actor need to know when the process have finished
                context.watch(currentActorRef)
                // TODO urlspatterns<=varconf
                val processUrlFilmMessage = ProccessPage(
                  s"http://mejorenvo.com/p${urlpagenumber}.html",
                  optActorRef.fold(self)(ref=>ref)
                )
                sendParserUrlMessage(currentActorRef, processUrlFilmMessage)
              })
    case parsedMoviePages: VoMoviePages =>
      parsedMoviePages.parsedPages match {
        case Result(_,Some(titlesMoviePage))=>
          log.info(
            s"An operation have done. ${includeconf} moviePageTitle:${titlesMoviePage.moviePageTitle} moviePageUrl:${titlesMoviePage.UrlPage}"
          )
        case Result(Some(failErrorInMoviePage),_) =>
          log.error(
            s"MoviePage error tags:${failErrorInMoviePage.failuresTags} moviePageUrl:${failErrorInMoviePage.message}"
          )
        case _ =>
          /** It remove warning compilation*/
          log.error(
          s"MoviePage Unkown error"
        )
      }
    case Terminated(urlMoviePage) =>
      watched -= urlMoviePage
      if (watched.isEmpty){
        val iOTask: ActorRef = context.actorOf(IOTask.props)
        sendIOMessage(iOTask, FinishSession(self))
      }

    /**
      * For validations that you should need to apply to your process
      *
      * case ValidateIoTOperations =>
      *   val iOTask: ActorRef = context.actorOf(IOTask.props)
      *   sendIOMessage ValidateOperations
      *
      */

    case ErrorStopActor =>
      val stopSystem: ActorRef = context.actorOf(KillSystem.props)
      log.error("Stopping Daemons For Errors")
      stopSystem ! ShutdownMessage
      context.stop(self)

    case StopProcess =>
      val stopSystem: ActorRef = context.actorOf(KillSystem.props)
      //Only stop BootDaemon
      //TODO consider when BootScheduled Need to Stop !!
      if(self.path.name == ConstantsObject.NotScheduledIoTDaemonProcessing){
        stopSystem ! ShutdownMessage
        context.stop(self)
      }
  }
}