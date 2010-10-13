package ar.noxit.paralleleditor.common.messages

import scala.serializable

/**
 * Clase base de los mensajes remotos
 */
@serializable
abstract case class BaseRemoteMessage

@serializable
case class SyncStatus(val myMsgs: Int, val otherMessages: Int)

/**
 * Clase base para las operaciones sobre documentos
 */
abstract case class RemoteOperation(val docTitle: String, val syncStatus: SyncStatus) extends BaseRemoteMessage

/**
 * Agregar texto
 */
case class RemoteAddText(override val docTitle: String, override val syncStatus: SyncStatus, val text: String, val startPos: Int)
        extends RemoteOperation(docTitle, syncStatus)

/**
 * Borrar texto
 */
case class RemoteDeleteText(override val docTitle: String, override val syncStatus: SyncStatus, val startPos: Int, val size: Int)
        extends RemoteOperation(docTitle, syncStatus)

/**
 * Composite operation
 */
case class CompositeRemoteOperation(override val docTitle: String, override val syncStatus: SyncStatus, val ops: RemoteOperation*)
        extends RemoteOperation(docTitle, syncStatus)

/**
 * Pide un nuevo documento
 */
case class RemoteNewDocumentRequest(val title: String) extends BaseRemoteMessage

/**
 * Suscribirse a un doc existe
 */
case class RemoteSubscribeRequest(val title: String) extends BaseRemoteMessage

/**
 * Suscripcion aceptada
 */
case class RemoteDocumentSubscriptionResponse(val docTitle: String, val initialContent: String) extends BaseRemoteMessage

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





