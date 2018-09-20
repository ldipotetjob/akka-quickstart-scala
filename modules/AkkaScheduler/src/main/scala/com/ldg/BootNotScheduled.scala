package com.ldg

import akka.actor.ActorSystem
import com.ldg.actors.IoTAdmin
import com.ldg.actors.IoTAdmin.ParseUrlFilms
import com.ldg.utilities.{ConstantsObject, ScalaLogger}

import scala.util.{Failure, Success, Try}


object BootNotScheduled extends App with ScalaLogger{

  val moviePageBoundaries: List[Int] = Try(List(args(0).toInt, args(1).toInt)) match {
    case Success(expectedList) => expectedList
    case  Failure(e) => log.error("Error with the boundaries of your page numbers,reviews your parameters {}",e.toString)
      System.exit(1)
      Nil
  }

  // This actor will control the whole System ("IoTDaemon_Not_Scheduled")
  val system = ActorSystem(ConstantsObject.NotScheduledIoTDaemon)
  val ioTAdmin = system.actorOf(IoTAdmin.props, ConstantsObject.NotScheduledIoTDaemonProcessing)
  val filteredAndOrderedList = moviePageBoundaries.toSet.toList.sorted
  val filteredAndOrderedListGamesBoundaries = (filteredAndOrderedList.head to filteredAndOrderedList.last).toList
  ioTAdmin!ParseUrlFilms(filteredAndOrderedListGamesBoundaries,Option(ioTAdmin))
}