package ar.noxit.paralleleditor.common.network

import actors.Actor

trait SenderActor {
    def !(msg: Any)
}

class SenderActorAdapter(val actor: Actor) extends SenderActor {
    def !(msg: Any) = actor ! msg
}

object SenderActor {
    implicit def fromActor2SenderActor(actor: Actor) =
        new SenderActorAdapter(actor)
}
