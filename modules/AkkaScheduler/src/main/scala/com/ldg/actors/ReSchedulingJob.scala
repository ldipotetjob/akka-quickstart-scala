package com.ldg.actors

import java.util.TimeZone

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import com.ldg.actors.ReSchedulingJob.FireSchedule
import com.ldg.utilities.ConfigurationBroker
import com.ldg.BootScheduled.system
import com.ldg.actors.IoTAdmin.ParseUrlFilms

object ReSchedulingJob {

  final case class FireSchedule(actorRef: ActorRef)

  def props: Props = Props(new ReSchedulingJob)

}

/**
  *
  * This actor creates a NEW scheduler every time that receive a message to create a
  * new Schedule taking as a reference a moviepagemejorenvo calendar.
  *
  * FireSchedule: Need execute rescheduleJob because it let us send a message to an specific actor
  *
  * https://github.com/enragedginger/akka-quartz-scheduler
  *
  */
class ReSchedulingJob extends Actor with ActorLogging with ConfigurationBroker{

  val defaultTimezone = TimeZone.getTimeZone("Europe/London")

  override def preStart(): Unit = log.info("Scheduling engine started")
  //TODO never get POSTSTOP
  override def postStop(): Unit = log.info("Scheduling engine stopped")


  def receive = {
    // TODO implement some kind of validation about like IotAdmin!ValidateConnection every time that the actor be fired
    case FireSchedule(scheduledDaemon) =>

      cronExpreesionMatches.foreach(
        cronExpreesionMatch => {
          val (cronExpreesion, listOfMoviePages) = cronExpreesionMatch
          log.info(
            "Programming Schedule for cronexpression: {} including matches: {}",
            cronExpreesion,listOfMoviePages.mkString("-")
          )
          val headList = listOfMoviePages.head

          // TODO it is important to kill all Scheduled jobs that has been created ONCE time that the work has done

          /** This code generate a warning because the reschedule job never exist
            * any more because headList used for named it never is the same*/
          QuartzSchedulerExtension(system).rescheduleJob(
            s"Movie-Page-Scheduler$headList",
            scheduledDaemon,
            ParseUrlFilms(listOfMoviePages),
            Option("Scheduling "), cronExpreesion, None, defaultTimezone)
        }
      )
      log.info("schedule get new MoviePages")
  }
}