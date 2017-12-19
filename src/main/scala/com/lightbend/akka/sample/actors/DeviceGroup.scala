package com.lightbend.akka.sample.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.lightbend.akka.sample.actors.DeviceGroup.{ReplyDeviceList, RequestAllTemperatures, RequestDeviceList}
import com.lightbend.akka.sample.actors.DeviceManager.RequestTrackDevice
import com.lightbend.akka.sample.actors.queries.DeviceGroupQuery

import scala.concurrent.duration._

/**
  *
  * requestId: As it's name indicate it is the id of who make the request
  *
  * groupId: As it's name indicate is the name of the group, an in our case each group
  * correspond to a group of house in the same neighborhood.
  *
  * deviceId: As it's name indicate is the deviceId, correspond to a unique device. That means for example:
  * for groupId1 there are deviceId1.1,deviceId1.1..deviceId1.n that represent sensors in house number 1
  *
  */


object DeviceGroup {
  def props(groupId: String): Props = Props(new DeviceGroup(groupId))

  final case class RequestDeviceList(requestId: Long)
  final case class ReplyDeviceList(requestId: Long, ids: Set[String])

  final case class RequestAllTemperatures(requestId: Long)
  final case class RespondAllTemperatures(requestId: Long, temperatures: Map[String, TemperatureReading])

  sealed trait TemperatureReading
  final case class Temperature(value: Double) extends TemperatureReading
  case object TemperatureNotAvailable extends TemperatureReading
  case object DeviceNotAvailable extends TemperatureReading
  case object DeviceTimedOut extends TemperatureReading
}

class DeviceGroup(groupId: String) extends Actor with ActorLogging {

  /** Maps that contains all devices registered in our System with the reference IdDevice => ActorReference*/
  var deviceIdToActor = Map.empty[String, ActorRef]
  /** Maps that contains all devices registered in our System with the reference ActorReference => IdDevice*/
  var actorToDeviceId = Map.empty[ActorRef, String]

  var nextCollectionId = 0L


  override def preStart(): Unit = log.info("DeviceGroup {} started", groupId)

  override def postStop(): Unit = log.info("DeviceGroup {} stopped", groupId)

  override def receive: Receive = {
    case trackMsg @ RequestTrackDevice(`groupId`, _) =>
      deviceIdToActor.get(trackMsg.deviceId) match {
        case Some(deviceActor) ⇒
          deviceActor forward trackMsg
        case None ⇒
          log.info("Creating device actor for {}", trackMsg.deviceId)
          val deviceActor = context.actorOf(Device.props(groupId, trackMsg.deviceId), s"device-${trackMsg.deviceId}")
          context.watch(deviceActor)
          actorToDeviceId += deviceActor -> trackMsg.deviceId
          deviceIdToActor += trackMsg.deviceId -> deviceActor
          deviceActor forward trackMsg
      }

    case RequestTrackDevice(groupId, deviceId) ⇒
      log.warning(
        "Ignoring TrackDevice request for {}. This actor is responsible for {}.",
        groupId, this.groupId
      )

    case RequestDeviceList(requestId) =>
      sender() ! ReplyDeviceList(requestId, deviceIdToActor.keySet)

    case Terminated(deviceActor) =>
      val deviceId = actorToDeviceId(deviceActor)
      log.info("Device actor for {} has been terminated", deviceId)
      actorToDeviceId -= deviceActor
      deviceIdToActor -= deviceId

    case RequestAllTemperatures(requestId) =>
      context.actorOf(DeviceGroupQuery.props(
        actorToDeviceId = actorToDeviceId,
        requestId = requestId,
        requester = sender(),
        3.seconds
      ))

  }
}