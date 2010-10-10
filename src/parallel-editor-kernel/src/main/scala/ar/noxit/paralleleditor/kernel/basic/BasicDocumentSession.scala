package ar.noxit.paralleleditor.kernel

import basic.DocumentActor
import messages.{ProcessOperation, Unsubscribe}
import ar.noxit.paralleleditor.common.operation.EditOperation

class BasicDocumentSession(val session: Session, val documentActor: DocumentActor) extends DocumentSession {
    val title = documentActor.title

    override def applyChange(operation: EditOperation) = {
        documentActor ! ProcessOperation(session, operation)
    }

    override def unsubscribe = {
        documentActor ! Unsubscribe(session)
    }
}
