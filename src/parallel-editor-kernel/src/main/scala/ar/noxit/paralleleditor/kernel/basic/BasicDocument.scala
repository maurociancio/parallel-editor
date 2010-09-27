package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import scala.List
import scala.actors.Actor

class BasicDocument(val title: String, var data: String, val actor: Actor) extends Document with DocumentData {

    private var subscribers: List[Session] = List()

    override def subscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (subscribers contains session)
            throw new DocumentSubscriptionAlreadyExistsException("the session is already suscribed to this document")

        subscribers = session :: subscribers
        new BasicDocumentSession(session, actor)
    }

    def unsubscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (!subscribers.contains(session))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers = subscribers filter { _ != session}
    }

    def silentUnsubscribe(session: Session) = {
        try {
            this unsubscribe session
        } catch  {
            case e: DocumentSubscriptionNotExistsException => null
        }
    }

    def subscriberCount = {
        subscribers size
    }
}
