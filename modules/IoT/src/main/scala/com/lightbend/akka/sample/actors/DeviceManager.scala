package com.lightbend.akka.sample.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.lightbend.akka.sample.actors.DeviceManager.RequestTrackDevice

/**
  * Creating device manager actors
  *
  * creates device group actors
  *
  */

object DeviceManager {
  //tell how build the DeviceManager
  def props(): Props = Props(new DeviceManager)
  final case class RequestTrackDevice(groupId: String, deviceId: String)
  case object DeviceRegistered
}

class DeviceManager extends Actor with ActorLogging {
  var groupIdToActor = Map.empty[String, ActorRef]
  var actorToGroupId = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("DeviceManager started")

  override def postStop(): Unit = log.info("DeviceManager stopped")

  /**
    * Akka provides a Death Watch feature that allows an actor to watch another actor and be notified if the other
    * actor is stopped. Unlike supervision, watching is not limited to parent-child relationships, any actor can
    * watch any other actor as long as it knows the ActorRef. After a watched actor stops, the watcher receives a
    * Terminated(actorRef) message which also contains the reference to the watched actor. The watcher can either
    * handle this message explicitly or will fail with a DeathPactException. This latter is useful if the actor
    * can no longer perform its own duties after the watched actor has been stopped. In our case, the group should
    * still function after one device have been stopped, so we need to handle the Terminated(actorRef) message.
    *
    */

  override def receive = {
    case trackMsg @ RequestTrackDevice(groupId, _) =>
      groupIdToActor.get(groupId) match {
        case Some(ref) =>
          //forrward a message(trackMsg) to actorRef(ref)
          ref forward trackMsg
        case None =>
          log.info("Creating device group actor for {}", groupId)
          val groupActor = context.actorOf(DeviceGroup.props(groupId), "group-" + groupId)
          //whatch the actor basically if something happen with it be informed
          context.watch(groupActor)
          //now that I've created the actor(groupActor) forward a message to it(trackMsg)
          groupActor forward trackMsg
          groupIdToActor += groupId -> groupActor
          actorToGroupId += groupActor -> groupId
      }

    case Terminated(groupActor) =>
      val groupId = actorToGroupId(groupActor)
      log.info("Device group actor for {} has been terminated", groupId)
      actorToGroupId -= groupActor
      groupIdToActor -= groupId

  }

}
