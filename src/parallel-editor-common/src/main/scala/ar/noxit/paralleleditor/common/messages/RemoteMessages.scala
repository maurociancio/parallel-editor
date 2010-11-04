package ar.noxit.paralleleditor.common.messages

import scala.serializable

/**
 * Clase base de los mensajes remotos
 */
@serializable
abstract case class BaseRemoteMessage

/**
 * Mensajes que van hacia el kernel y que deben convertirse antes de ser enviados
 */
@serializable
trait ToKernel

/**
 * Mensajes enviados desde el cliente que tienen destino al kernel, son todos request
 */

@serializable
trait Request

/**
 * Mensaje proveniente del kernel que son respuestas a mensajes
 */
@serializable
trait Response

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
case class RemoteNewDocumentRequest(val title: String, val initialContent: String = "") extends BaseRemoteMessage with ToKernel with Request

/**
 * Suscribirse a un doc existe
 */
case class RemoteSubscribeRequest(val title: String) extends BaseRemoteMessage with ToKernel with Request

/**
 *
 */
case class RemoteDocumentTitleExists(val offenderTitle: String) extends BaseRemoteMessage with Response

/**
 * Suscripcion aceptada
 */
case class RemoteDocumentSubscriptionResponse(val docTitle: String, val initialContent: String) extends BaseRemoteMessage with Response

/**
 * Ya existe subscripcion
 */
case class RemoteDocumentSubscriptionAlreadyExists(val offenderTitle: String) extends BaseRemoteMessage with Response

/**
 * No existe subscripcion
 */
case class RemoteDocumentSubscriptionNotExists(val offenderTitle: String) extends BaseRemoteMessage with Response

/**
 * No se puede borrar el documento, está en uso
 */
case class RemoteDocumentInUse(val docTitle: String) extends BaseRemoteMessage with Response

/**
 * El doc se borró ok
 */
case class RemoteDocumentDeletedOk(val docTitle: String) extends BaseRemoteMessage with Response

/**
 * El titulo no existe
 */
case class RemoteDocumentDeletionTitleNotExists(val docTitle: String) extends BaseRemoteMessage with Response

/**
 * Pide listado de usuarios
 */
case class RemoteUserListRequest extends BaseRemoteMessage with Request with ToKernel

/**
 * Pide listado de documentos
 */
case class RemoteDocumentListRequest extends BaseRemoteMessage with ToKernel with Request

/**
 * Respuesta listado de usuarios
 */
case class RemoteUserListResponse(val usernames: Map[String, List[String]]) extends BaseRemoteMessage with Response

/**
 * Respuesta de listado de documentos
 */
case class RemoteDocumentListResponse(val docList: List[String]) extends BaseRemoteMessage with Response

/**
 * Pide desuscriberse a un doc
 */
case class RemoteUnsubscribeRequest(val title: String) extends BaseRemoteMessage with Request

/**
 * Pide al kernel que borre un documento
 */
case class RemoteDeleteDocumentRequest(val docTitle: String) extends BaseRemoteMessage with Request with ToKernel

/**
 * Pide login
 */
case class RemoteLoginRequest(val username: String) extends BaseRemoteMessage with Request

/**
 * Rta de login OK
 */
case class RemoteLoginOkResponse extends BaseRemoteMessage with Response

/**
 * Rta de Login Erroneo
 */
abstract case class RemoteLoginRefusedRemoteResponse extends BaseRemoteMessage with Response

/**
 * Nombre de usuario tomado
 */
case class UsernameAlreadyExistsRemoteResponse extends RemoteLoginRefusedRemoteResponse with Response

/**
 * Pedido de logout
 */
case class RemoteLogoutRequest extends BaseRemoteMessage





