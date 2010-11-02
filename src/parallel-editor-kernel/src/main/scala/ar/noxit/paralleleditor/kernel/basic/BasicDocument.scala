package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import scala.List
import ar.noxit.paralleleditor.common.operation.DocumentData

class BasicDocument(val title: String, var data: String, private val docSessionFactory: DocumentSessionFactory) extends Document with DocumentData {
    private var subscribers = List[Session]()

    override def subscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (subscribers contains session)
            throw new DocumentSubscriptionAlreadyExistsException("the session is already suscribed to this document")

        subscribers = session :: subscribers
        docSessionFactory.newDocumentSession(this, session)
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

    def propagateToOthers(who: Session, what: Session => Unit) = {
        if (!subscribers.contains(who))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers.filter {s => s != who}.foreach {
            s => what(s)
        }
    }

    def delete(who: Session) = {
        if (!subscribers.contains(who))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        if (subscribers.size != 1)
            throw new DocumentInUseException("document is being used")

        subscribers = List()
    }

    def subscriberCount = {
        subscribers size
    }
}
