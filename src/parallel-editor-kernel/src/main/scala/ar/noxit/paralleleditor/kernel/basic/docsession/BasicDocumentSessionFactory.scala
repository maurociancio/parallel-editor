package ar.noxit.paralleleditor.kernel.basic.docsession

import ar.noxit.paralleleditor.kernel.{BasicDocumentSession, Session, Document}
import ar.noxit.paralleleditor.kernel.basic.{DocumentActor, DocumentSessionFactory}

class BasicDocumentSessionFactory extends DocumentSessionFactory {
    var docActor: DocumentActor = _

    def newDocumentSession(document: Document, who: Session) =
        new BasicDocumentSession(who, docActor)
}
