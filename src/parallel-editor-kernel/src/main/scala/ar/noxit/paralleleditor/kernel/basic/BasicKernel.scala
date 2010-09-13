package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.BasicDocumentHandler
import ar.noxit.paralleleditor.kernel.exceptions.DocumentTitleAlreadyExitsException
import ar.noxit.paralleleditor.kernel.Document
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.kernel.Kernel
import scala.List

class BasicKernel extends Kernel {

    var sessions: List[Session] = List()
    var documents: List[Document] = List()

    def login(username: String) = {
        val newSession = new BasicSession(username)
        sessions = newSession :: sessions

        newSession
    }

    def logout(session: Session) = {
        if (!sessions.contains(session))
            throw new IllegalArgumentException("invalid session") 

        sessions = sessions.filter{ _ == session }
    }

    def newDocument(owner: Session, title: String, initialContent: String) = {
        if (documents.exists{ _ == title })
            throw new DocumentTitleAlreadyExitsException("document title already exists")

        val newDocument = new BasicDocument(title, initialContent)
        documents = newDocument :: documents

        new BasicDocumentHandler(owner, newDocument)
    }
}