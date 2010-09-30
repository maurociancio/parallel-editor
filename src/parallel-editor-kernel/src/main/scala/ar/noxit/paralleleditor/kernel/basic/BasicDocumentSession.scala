package ar.noxit.paralleleditor.kernel

import messages.{ProcessOperation, Unsubscribe}
import scala.actors.Actor

class BasicDocumentSession(val session: Session, val documentActor: Actor) extends DocumentSession {

    override def applyChange(operation: EditOperation) = {
        documentActor ! ProcessOperation(session, operation)
    }

    override def unsubscribe = {
        documentActor ! Unsubscribe(session)
    }
}
