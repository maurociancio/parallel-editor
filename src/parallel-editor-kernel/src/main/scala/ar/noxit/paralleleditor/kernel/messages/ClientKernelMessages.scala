package ar.noxit.paralleleditor.kernel.messages

import scala.actors.Actor
import ar.noxit.paralleleditor.kernel.{DocumentSession, Session}

case class LoginRequest(val username: String)

case class LoginResponse(val session: Session) {
    if (session == null)
        throw new IllegalArgumentException("session cannot be null")
}

case class NewDocumentRequest(val session: Session, val title: String)
case class NewDocumentResponse(val docSession: DocumentSession)

case class DocumentListRequest(val session: Session)
case class DocumentListResponse(val documents: List[String])