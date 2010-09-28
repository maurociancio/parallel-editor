package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.exceptions.DocumentTitleAlreadyExitsException
import ar.noxit.paralleleditor.common.logger.Loggable
import messages.{Subscribe, SubscriberCount, SilentUnsubscribe}
import scala.List

class BasicKernel extends Kernel with Loggable {
    val timeout = 5000
    var sessions = List[Session]()
    var documents = List[DocumentActor]()

    override def login(username: String) = {
        val newSession = new BasicSession(username, this)
        sessions = newSession :: sessions

        newSession
    }

    override def newDocument(owner: Session, title: String, initialContent: String) = {
        if (documents exists {_.title == title})
            throw new DocumentTitleAlreadyExitsException("document title already exists")

        // create a document actor
        val newDocActor = newDocumentActor(title, initialContent)

        // add document to list
        documents = newDocActor :: documents

        // susbscribe owner to the document
        newDocActor ! Subscribe(owner)
    }

    protected def newDocumentActor(title: String, initialContent: String): DocumentActor = {
        // create new document factory
        val doc = new BasicDocumentFactory(title, initialContent)

        // create a document actor
        val actor = new BasicDocumentActor(doc)

        // start the actor
        actor.start

        actor
    }

    override def documentList = documents.map {_.title}

    def logout(session: Session) = {
        if (!sessions.contains(session))
            throw new IllegalArgumentException("session not logged in")

        sessions = sessions filter {_ != session}
        documents foreach {doc => doc ! SilentUnsubscribe(session)}
    }

    def documentCount = documents size

    def sessionCount = sessions size

    // TODO change this method to private
    def documentByTitle(docTitle: String) = documents find {_.title == docTitle}

    def documentSubscriberCount(docTitle: String) {
        documentByTitle(docTitle) match {
            case Some(doc) => {
                trace("Sending Subscriber Count Request")
                doc ! SubscriberCount()
            }
        }
    }
}
