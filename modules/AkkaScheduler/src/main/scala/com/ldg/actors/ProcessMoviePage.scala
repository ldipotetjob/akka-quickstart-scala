package com.ldg.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.ldg.actors.ProcessMoviePage.ProccessPage
import com.ldg.moviepages.VoMoviePages

object ProcessMoviePage {
  final case class ProccessPage(urMoviePage: Any,actorRef: ActorRef)
  def props = Props(new ProcessMoviePage)
}

/**
  * Process the page that receives as a parameter and the Stop the actor when it finishes
  * the processing
  */
class ProcessMoviePage extends Actor with ActorLogging {

  override def receive = {
    case ProccessPage(urMoviePage, actorRef) =>
      actorRef ! VoMoviePages(urMoviePage)
      context.stop(self)
  }
}
