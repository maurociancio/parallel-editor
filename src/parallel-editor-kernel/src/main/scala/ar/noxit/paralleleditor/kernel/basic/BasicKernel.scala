package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.common.logger.Loggable
import docsession.BasicDocumentSessionFactory
import exceptions.{UsernameAlreadyExistsException, DocumentTitleAlreadyExitsException}
import messages.{Subscribe, SubscriberCount, SilentUnsubscribe}
import scala.List
import sync.SynchronizerAdapterFactory

class BasicKernel extends Kernel with Loggable {
    val timeout = 5000
    var sessions = List[Session]()
    var documents = List[DocumentActor]()

    override def login(username: String) = {
        if (username == null)
            throw new IllegalArgumentException("username cannot be null")

        if (sessions.exists {s => s.username == username})
            throw new UsernameAlreadyExistsException("username already logged in")

        val newSession = new BasicSession(username, this)
        sessions = newSession :: sessions

        trace("new session")
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
        // session factory
        val docSessionFactory = new BasicDocumentSessionFactory

        // document
        val doc = new BasicDocument(title, initialContent, docSessionFactory)

        // create a document actor
        val actor = new BasicDocumentActor(doc, newSyncFactory)

        // set the document actor to the session factory
        docSessionFactory.docActor = actor

        // start the actor
        actor.start

        actor
    }

    protected def newSyncFactory: SynchronizerFactory = new SynchronizerAdapterFactory

    override def documentList = documents.map {_.title}

    override def subscribe(session: Session, title: String) = {
        val doc = documentByTitle(title)

        if (doc.isDefined) {
            doc.get ! Subscribe(session)
        } else {
            warn("title does not exists")
        }
    }

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

    def documentSubscriberCount(docTitle: String) = {
        documentByTitle(docTitle).map(docActor => {
            trace("Sending Subscriber Count Request")

            docActor !! (SubscriberCount(), {
                case count: Int => count
            })
        })
    }
}
