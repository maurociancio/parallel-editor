package ar.noxit.paralleleditor.kernel

import messages.Unsubscribe
import scala.actors.Actor

class BasicDocumentSession(val session: Session, val documentActor: Actor) extends DocumentSession {

    override def applyChange(operation: EditOperation) = {
        documentActor ! operation
    }

    override def unsubscribe = {
        documentActor ! Unsubscribe(session)
    }
}