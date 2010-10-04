package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.{DocumentSession, Session}

/**
 * Mensajes entre el actor Kernel y el actor del proxy del Cliente remoto
 */

case class LoginRequest(val username: String)

case class LoginResponse(val session: Session) {
    if (session == null)
        throw new IllegalArgumentException("session cannot be null")
}

case class NewDocumentRequest(val session: Session, val title: String)
case class NewDocumentResponse(val docSession: DocumentSession)

case class DocumentListRequest(val session: Session)
case class DocumentListResponse(val documents: List[String])

case class SubscribeToDocumentRequest(val session: Session, val title: String)
