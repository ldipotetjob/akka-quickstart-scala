package com.ldg.actors


import akka.actor.{Actor, Props}

object ShutdownMessage

object KillSystem {
  def props : Props = Props[KillSystem]
}

/**
  * Kill the actor system when receive the message.
  *
  * com.ldg.BootNotScheduled: call it when finish to process an specific
  * range of page numbers so when it probe that all pages have been scanned send a message to
  * this actor to stop the recursive process.
  *
  *  com.ldg.BootScheduled: there are ONLY ONE reason for call this actor, when something
  *  was wrong, a big RUNTIME ERROR, so in this case you have to stop the system of actors.
  *
  */

// TODO classify what error can be include in this category: big RUNTIME ERROR
class KillSystem extends Actor {
  def receive = {
    case ShutdownMessage => context.system.terminate()
      context.stop(self)
    case _ =>
  }
}