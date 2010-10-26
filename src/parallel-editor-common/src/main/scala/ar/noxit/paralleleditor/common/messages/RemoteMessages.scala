package ar.noxit.paralleleditor.common.messages

import scala.serializable

/**
 * Clase base de los mensajes remotos
 */
@serializable
abstract case class BaseRemoteMessage

/**
 * Mensajes que van hacia el kernel
 */
@serializable
trait ToKernel

/**
 * Nivel documento
 */

/**
 * Clase base para las operaciones sobre documentos
 */
abstract case class RemoteOperation extends BaseRemoteMessage

/**
 * Agregar texto
 */
case class RemoteAddText(val text: String, val startPos: Int, val pword: List[Int]) extends RemoteOperation

/**
 * Borrar texto
 */
case class RemoteDeleteText(val startPos: Int, val size: Int) extends RemoteOperation

/**
 * Null operation
 */
case class RemoteNullOpText extends RemoteOperation

/**
 * A nivel Sincronismo
 */

/**
 * información de sincronización del documento
 */
@serializable
case class SyncStatus(val myMsgs: Int, val otherMessages: Int)

/**
 * Sincronizar operaciones
 */
case class SyncOperation(val syncSatus: SyncStatus, val payload: RemoteOperation) extends BaseRemoteMessage

/**
 * A nivel kernel
 */

/**
 * Operacion sobre un determinado documento
 */
case class RemoteDocumentOperation(val docTitle: String, val payload: SyncOperation) extends BaseRemoteMessage



/**
 * Pide un nuevo documento
 */
case class RemoteNewDocumentRequest(val title: String, val initialContent: String = "") extends BaseRemoteMessage with ToKernel

/**
 * Suscribirse a un doc existe
 */
case class RemoteSubscribeRequest(val title: String) extends BaseRemoteMessage with ToKernel

/**
 *
 */
case class RemoteDocumentTitleExists(val offenderTitle: String) extends BaseRemoteMessage

/**
 * Suscripcion aceptada
 */
case class RemoteDocumentSubscriptionResponse(val docTitle: String, val initialContent: String) extends BaseRemoteMessage

/**
 * Ya existe subscripcion
 */
case class RemoteDocumentSubscriptionAlreadyExists(val offenderTitle: String) extends BaseRemoteMessage

/**
 * No existe subscripcion
 */
case class RemoteDocumentSubscriptionNotExists(val offenderTitle: String) extends BaseRemoteMessage

/**
 * Pide listado de documentos
 */
case class RemoteDocumentListRequest extends BaseRemoteMessage with ToKernel

/**
 * Respuesta de listado de documentos
 */
case class RemoteDocumentListResponse(val docList: List[String]) extends BaseRemoteMessage

/**
 * Pide desuscriberse a un doc
 */
case class RemoteUnsubscribeRequest(val title: String) extends BaseRemoteMessage

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
abstract case class RemoteLoginRefusedRemoteResponse extends BaseRemoteMessage

/**
 * Nombre de usuario tomado
 */
case class UsernameAlreadyExistsRemoteResponse extends RemoteLoginRefusedRemoteResponse

/**
 * Pedido de logout
 */
case class RemoteLogoutRequest extends BaseRemoteMessage





