package com.ldg.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.ldg.actors.IOTask.FinishSession
import com.ldg.actors.IoTAdmin.StopProcess

/** for input/output task: create  */
object IOTask {
  sealed class IOTaskMessages
  final case class FinishSession(actorRefValidator:ActorRef) extends IOTaskMessages
  def props = Props(new IOTask)
}

/**
  * Actor for Input/Output Task:
  *  PreStartTest:
  *  - Validate every thing tha we could need
  *
  *  If the aforementioned validations fail the actor system is stopped
  *
  * FinishSession:
  * - receive the order to stop the process. It will happen only in BootNotScheduled
  *   or when something was wrong.
  *
  */
class IOTask extends Actor with ActorLogging {

  // TODO create a parameter or SOMETHING to identify the process in the actor that has started or stoped
  override def preStart(): Unit = log.info("Start IOTask process")
  override def postStop(): Unit = log.info("Stop IOTask process")

  override def receive = {
    /**
      * case PreStartTest =>
      * ValidateIoTOperations
      * context.stop(self)
      *
      */

    case FinishSession(actorRefValidator) =>
      actorRefValidator ! StopProcess
      context.stop(self)
  }
}