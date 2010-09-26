package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.exceptions.DocumentTitleAlreadyExitsException
import scala.List

class BasicKernel extends Kernel {

    var sessions: List[Session] = List()
    var documents: List[BasicDocument] = List()

    def login(username: String) = {
        val newSession = new BasicSession(username, this)
        sessions = newSession :: sessions

        newSession
    }

    def logout(session: Session) = {
        if (!sessions contains session)
            throw new IllegalArgumentException("session not logged in")

        sessions = sessions filter { _ != session }
        documents foreach {doc => doc silentUnsubscribe session}
    }

    def newDocument(owner: Session, title: String, initialContent: String) = {
        if (documents exists { _.title == title })
            throw new DocumentTitleAlreadyExitsException("document title already exists")

        val newDocument = new BasicDocument(title, initialContent)
        documents = newDocument :: documents

        newDocument subscribe owner
    }

    def documentList = documents.map { _.title }

    def documentCount = documents size

    def sessionCount = sessions size

    def documentByTitle(docTitle: String) = documents find { _.title == docTitle }

    def documentSubscriberCount(docTitle: String) = {
        val doc = documentByTitle(docTitle)
        if (doc isEmpty)
            None
        else
            Some((doc.get).subscriberCount)
    }
}