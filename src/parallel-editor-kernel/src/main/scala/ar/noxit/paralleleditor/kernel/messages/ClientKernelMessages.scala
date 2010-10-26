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

case class UsernameAlreadyExists

case class NewDocumentRequest(val session: Session, val title: String, val initialContent: String)

/**
 * Generado cuando la suscripción a un documento fue exitosa.
 * Puede ser enviado tanto si es para un nuevo documento como para uno existente
 */
case class SubscriptionResponse(val docSession: DocumentSession, val initialContent: String)

/**
 * Generado cuando una sesión ya está suscripta a un mensaje
 */
case class SubscriptionAlreadyExists(val offenderTitle: String)

/**
 * Generado cuando se solicita desuscripción a un documento no suscripto
 */
case class SubscriptionNotExists(val offenderTitle: String)

case class DocumentListRequest(val session: Session)
case class DocumentListResponse(val documents: List[String])

case class SubscribeToDocumentRequest(val session: Session, val title: String)
case class UnsubscribeToDocumentRequest(val session: Session, val title: String)
