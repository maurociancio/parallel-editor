package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import scala.List

class BasicDocument(val title: String, var content: String) extends Document {

    var subscribers: List[Session] = List()

    def subscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (subscribers exists { _ == session})
            throw new DocumentSubscriptionAlreadyExistsException("the session is already suscribed to this document")

        subscribers = session :: subscribers
        new BasicDocumentSession(session, this)
    }

    def unsubscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (!subscribers.exists { _ == session})
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