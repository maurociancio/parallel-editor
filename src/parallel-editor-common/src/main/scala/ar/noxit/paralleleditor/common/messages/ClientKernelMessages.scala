package ar.noxit.paralleleditor.common.messages

import scala.serializable

/**
 * Clase base de los mensajes remotos
 */
@serializable
abstract case class BaseRemoteMessage

/**
 * Clase base para las operaciones sobre documentos
 */
abstract case class RemoteOperation extends BaseRemoteMessage

/**
 * Agregar texto
 */
case class RemoteAddText(val text: String, val startPos: Int) extends RemoteOperation

/**
 * Borrar texto
 */
case class RemoteDeleteText(val startPos: Int, val size: Int) extends RemoteOperation

/**
 * Composite operation
 */
case class CompositeRemoteOperation(val ops: RemoteOperation*) extends RemoteOperation

/**
 * Pide un nuevo documento
 */
case class RemoteNewDocumentRequest(val title: String) extends BaseRemoteMessage

/**
 * Suscripcion aceptada
 */
case class RemoteDocumentSubscriptionResponse(val initialContent: String) extends BaseRemoteMessage

/**
 * Pide listado de documentos
 */
case class RemoteDocumentListRequest extends BaseRemoteMessage

/**
 * Respuesta de listado de documentos
 */
case class RemoteDocumentListResponse(val docList: List[String]) extends BaseRemoteMessage

/**
 * Pide login
 */
case class RemoteLoginRequest(val username: String) extends BaseRemoteMessage

/**
 * Rta de login OK
 */
case class RemoteLoginOkResponse extends BaseRemoteMessage

/**
 * Rta de Login Erroneo
 */
case class RemoteLoginRefusedResponse(val reason: String) extends BaseRemoteMessage

/**
 * Pedido de logout
 */
case class RemoteLogoutRequest extends BaseRemoteMessage





