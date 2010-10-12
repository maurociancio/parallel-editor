package ar.noxit.paralleleditor.gui

import swing.event.Event

/**
 * Eventos de la GUI, generados desde los controles hacia las reactions que estan en la GUI
 */

case class ConnectionRequest(val host: String, val port: Int) extends Event
case class DisconnectionRequest extends Event
case class DocumentListRequest extends Event
case class NewDocumentRequest(val docTitle: String, val initialContent: String = "") extends Event
case class CloseCurrentDocument() extends Event

/**
 * Eventos publicados por el document area
 */

/**
 * Operacion de edicion sobre un documento
 */
abstract class EditionEvent(val docTitle: String) extends Event
case class InsertionEvent(override val docTitle: String, val pos: Int, val text: String) extends EditionEvent(docTitle)
case class DeletionEvent(override val docTitle: String, val pos: Int, val count: Int) extends EditionEvent(docTitle)

/**
 * Publicado por el frame de documentos
 */
case class SubscribeToDocument(val title: String) extends Event

/**
 * Wrapper de eventos para evitar bucles infinitos
 */
case class WrappedEvent(val event: Event) extends Event
