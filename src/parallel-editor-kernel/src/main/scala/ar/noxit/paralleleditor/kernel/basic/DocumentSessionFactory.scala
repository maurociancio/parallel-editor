package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.{DocumentSession, Document, Session}

trait DocumentSessionFactory {
    def newDocumentSession(document: Document, who: Session): DocumentSession
}
