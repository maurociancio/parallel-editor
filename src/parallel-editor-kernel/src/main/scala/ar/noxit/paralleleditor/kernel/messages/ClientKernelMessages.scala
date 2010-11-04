package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.{DocumentSession, Session}

/**
 * Se aplica a los mensajes que son convertibles hacia mensajes remotos
 */
trait ToRemote

/**
 * Mensajes entre el actor Kernel y el actor del proxy del Cliente remoto
 */

case class LoginRequest(val username: String)

case class LoginResponse(val session: Session) {
    if (session == null)
        throw new IllegalArgumentException("session cannot be null")
}

case class UsernameAlreadyExists

case class DocumentTitleExists(val offenderTitle: String) extends ToRemote

case class DocumentDeletionTitleNotExists(val offenderTitle: String) extends ToRemote

case class NewDocumentRequest(val session: Session, val title: String, val initialContent: String)

case class CloseDocument(val session: Session, val docTitle: String)

case class UserListRequest(val session: Session)

case class UserListResponse(val usernames: Map[String, List[String]]) extends ToRemote

/**
 * Generado cuando la suscripción a un documento fue exitosa.
 * Puede ser enviado tanto si es para un nuevo documento como para uno existente
 */
case class SubscriptionResponse(val docSession: DocumentSession, val initialContent: String) extends ToRemote

/**
 * Generado cuando una sesión ya está suscripta a un mensaje
 */
case class SubscriptionAlreadyExists(val offenderTitle: String) extends ToRemote

/**
 * Generado cuando se solicita desuscripción a un documento no suscripto
 */
case class SubscriptionNotExists(val offenderTitle: String) extends ToRemote

case class DocumentDeletedOk(val docTitle: String) extends ToRemote

case class DocumentInUse(val docTitle: String) extends ToRemote

case class DocumentListRequest(val session: Session)
case class DocumentListResponse(val documents: List[String]) extends ToRemote

case class SubscribeToDocumentRequest(val session: Session, val title: String)
case class UnsubscribeToDocumentRequest(val session: Session, val title: String)
