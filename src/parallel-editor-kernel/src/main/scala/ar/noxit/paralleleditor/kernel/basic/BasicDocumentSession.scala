package ar.noxit.paralleleditor.kernel

import basic.DocumentActor
import messages.{ProcessOperation, Unsubscribe}
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.Message

class BasicDocumentSession(val session: Session, val documentActor: DocumentActor) extends DocumentSession {
    val title = documentActor.title

    override def applyChange(m: Message[EditOperation]) =
        documentActor ! ProcessOperation(session, m)

    override def unsubscribe =
        documentActor ! Unsubscribe(session)
}
