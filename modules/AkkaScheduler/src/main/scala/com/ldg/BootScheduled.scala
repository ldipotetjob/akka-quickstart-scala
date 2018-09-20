package com.ldg

import akka.actor.ActorSystem
import com.ldg.actors.{IoTAdmin, ReSchedulingJob}
import com.ldg.actors.ReSchedulingJob.FireSchedule
import com.ldg.utilities.ConstantsObject
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

object BootScheduled extends App {
  //https://github.com/lambdista/config
  val system = ActorSystem(ConstantsObject.ScheduledIoTDaemon)
  // This actor will control the whole System ("IoTDaemon_Scheduled")
  val daemonScheduled = system.actorOf(IoTAdmin.props, ConstantsObject.ScheduledIoTDaemonProcessing)
  // This actor will control the reScheduling about time table for update new pages with films in original version
  val reeschedule = system.actorOf(ReSchedulingJob.props, ConstantsObject.DaemonReShedulingVoMoviePages)
  //Use system's dispatcher as ExecutionContext
  //QuartzSchedulerExtension is scoped to that ActorSystem and there will only ever be one instance of it per ActorSystem
  QuartzSchedulerExtension(system).schedule("moviepages", reeschedule, FireSchedule(daemonScheduled))
}