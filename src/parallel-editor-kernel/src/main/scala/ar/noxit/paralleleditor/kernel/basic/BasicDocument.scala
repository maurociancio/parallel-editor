package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import messages.{SubscriptionCancelled, SubscriberLeftDocument, NewSubscriberToDocument}
import scala.List
import ar.noxit.paralleleditor.common.operation.{Caret, DocumentData}

class BasicDocument(val title: String, var data: String, private val docSessionFactory: DocumentSessionFactory) extends Document with DocumentData {
    private var subscribers = List[Session]()

    override def subscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (subscribers contains session)
            throw new DocumentSubscriptionAlreadyExistsException("the session is already suscribed to this document")

        // notify users
        subscribers.foreach {target => target notifyUpdate NewSubscriberToDocument(session.username, title)}

        // add the new session
        subscribers = session :: subscribers

        // new doc session
        docSessionFactory.newDocumentSession(this, session)
    }

    def unsubscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (!subscribers.contains(session))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers = subscribers filter {_ != session}

        // notify the subscribers
        subscribers.foreach {target => target notifyUpdate SubscriberLeftDocument(session.username, title)}

        session notifyUpdate SubscriptionCancelled(title)
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

    val caret = new Caret {
        val selectionLength = 0 // caret at pos 0
        val offset = 0 // nothing selected

        override def change(offset: Int, selectionLength: Int) = null // do nothing}
    }


    override def replace(offset: Int, length: Int, newText: String) =
        data = data.substring(0, offset) + (if (newText == null) "" else newText) + "" + data.substring(offset + length)

    def subscriberCount = subscribers size

    def subscribersNames = subscribers.map {s => s.username}
}
