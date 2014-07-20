package de.windelknecht.stup.utils.coding.akka

import akka.actor.Actor

trait ChainActor
  extends Actor {
  var receivers: Actor.Receive = Actor.emptyBehavior
  def receiver(next: Actor.Receive) { receivers = receivers orElse next }
  def receive = receivers // Actor.receive definition
}
