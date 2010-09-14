package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.basic._
import scala.List

class BasicDocument(val title: String, var content: String) extends Document {

    var suscribers: List[Session] =  List()

    def suscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (suscribers exists { _ == session})
            throw new DocumentSuscriptionAlreadyExistsException("the session is already suscribed to this document")

        suscribers = session :: suscribers
        new BasicDocumentHandler(session, this)
    }

    def unsuscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (!suscribers.exists { _ == session})
            throw new DocumentSuscriptionNotExistsException("the session is not suscribed to this document")

        suscribers = suscribers filter { _ != session}
    }

    def silentUnsuscribe(session: Session) = {
        try {
            this unsuscribe session
        } catch  {
            case e: DocumentSuscriptionNotExistsException => null
        }
    }

    def suscriberCount = {
        suscribers size
    }
}