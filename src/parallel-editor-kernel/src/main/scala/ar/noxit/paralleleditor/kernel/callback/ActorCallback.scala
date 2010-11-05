package ar.noxit.paralleleditor.kernel.callback

import scala.actors.Actor
import ar.noxit.paralleleditor.kernel.UpdateCallback

class ActorCallback(val actor: Actor) extends UpdateCallback {
    if (actor == null)
        throw new IllegalArgumentException("actor cannot be null")

    def update(message: Any) = actor ! message
}
