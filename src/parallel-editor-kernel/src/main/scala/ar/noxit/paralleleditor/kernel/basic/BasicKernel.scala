package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.common.logger.Loggable
import docsession.BasicDocumentSessionFactory
import exceptions.{SessionNotExistsException, DocumentDeleteUnexistantException, UsernameAlreadyExistsException, DocumentTitleAlreadyExitsException}
import messages._
import scala.List
import reflect.BeanProperty
import scala.actors.TIMEOUT
import scala.actors.Actor.{actor, loopWhile, receiveWithin}

class BasicKernel extends Kernel with Loggable {
    @BeanProperty
    var timeout: Int = _
    @BeanProperty
    var sync: SynchronizerFactory = _

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

    override def deleteDocument(session: Session, title: String) {
        if (!sessions.contains(session))
            throw new SessionNotExistsException("session not exists")

        val doc = documentByTitle(title).getOrElse {
            throw new DocumentDeleteUnexistantException("document " + title + " does not exist")
        }

        // enviamos msg al documento
        doc ! Close(session)
    }

    protected def newDocumentActor(title: String, initialContent: String): DocumentActor = {
        // session factory
        val docSessionFactory = new BasicDocumentSessionFactory

        // document
        val doc = new BasicDocument(title, initialContent, docSessionFactory)

        // create a document actor
        val actor = new BasicDocumentActor(doc, newSyncFactory)
        // set the timeout
        actor.timeout = this.timeout

        // set the document actor to the session factory
        docSessionFactory.docActor = actor

        // start the actor
        actor.start

        actor
    }

    def removeDeletedDocument(title: String) {
        documents = documents.filter {doc => doc.title != title}
    }

    protected def newSyncFactory: SynchronizerFactory = sync

    override def documentList = documents.map {_.title}

    override def subscribe(session: Session, title: String) = {
        val doc = documentByTitle(title)

        // TODO tirar excepcion si no existe el title
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

    override def userList(session: Session) = {
        var usernames = sessions.map {s => (s.username, List[String]())}.toMap
        val docs = documents

        actor {
            trace("counter actor started")
            docs.foreach {doc => doc ! DocumentUserListRequest()}

            var received = 0
            val total = docs.size

            val notifySession = {
                session notifyUpdate UserListResponse(usernames)
            }

            loopWhile(received < total) {
                receiveWithin(timeout) {
                    case DocumentUserListResponse(docTitle, users) => {
                        users.foreach {
                            username => usernames = usernames.updated(username, docTitle :: usernames(username))
                        }
                        received = received + 1
                        if (received == total)
                            notifySession
                    }
                    case TIMEOUT => {
                        warn("document did not respond to user list req")
                        notifySession
                    }
                }
            }
        }
    }

    def documentCount = documents size

    def sessionCount = sessions size

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
