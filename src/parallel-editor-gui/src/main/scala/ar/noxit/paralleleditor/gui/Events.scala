package ar.noxit.paralleleditor.gui

import swing.event.Event
import ar.noxit.paralleleditor.common.operation.DocumentOperation

/**
 * Eventos de la GUI, generados desde los controles hacia las reactions que estan en la GUI
 */

case class ConnectionRequest(val host: String, val port: Int) extends Event
case class DisconnectionRequest extends Event
case class DocumentListRequest extends Event
case class NewDocumentRequest(val docTitle: String, val initialContent: String = "") extends Event
case class CloseCurrentDocument extends Event
case class DeleteCurrentDocument extends Event

/**
 * Eventos publicados por el document area
 */

/**
 * Operacion de edicion sobre un documento
 */
abstract class EditionEvent extends Event
case class InsertionEvent(val pos: Int, val text: String) extends EditionEvent
case class DeletionEvent(val pos: Int, val count: Int) extends EditionEvent

/**
 * Publicado por el frame de documentos
 */
case class SubscribeToDocument(val title: String) extends Event


case class OperationEvent(val docOp: DocumentOperation) extends Event

/**
 * Wrapper de eventos para evitar bucles infinitos
 */
case class WrappedEvent(val event: Event) extends Event
