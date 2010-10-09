package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import messages.PublishOperation
import scala.List
import scala.actors.Actor
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.operation.DocumentData

class BasicDocument(val title: String, var data: String, val docActor: Actor) extends Document with DocumentData {
    private var subscribers: List[Session] = List()

    override def subscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (subscribers contains session)
            throw new DocumentSubscriptionAlreadyExistsException("the session is already suscribed to this document")

        subscribers = session :: subscribers
        // TODO agregar factory acá para esconder el actor
        new BasicDocumentSession(title, session, docActor)
    }

    def unsubscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (!subscribers.contains(session))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers = subscribers filter {_ != session}
    }

    def silentUnsubscribe(session: Session) = {
        try {
            this unsubscribe session
        } catch {
            case e: DocumentSubscriptionNotExistsException => null
        }
    }

    def propagateOperation(session: Session, operation: EditOperation) = {
        if (!subscribers.contains(session))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers.filter {s => s != session}.foreach {
            s =>
                val op = PublishOperation(title, operation)
                s notifyUpdate op
        }
    }

    def subscriberCount = {
        subscribers size
    }
}
