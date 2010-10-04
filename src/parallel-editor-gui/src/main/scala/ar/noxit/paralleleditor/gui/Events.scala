package ar.noxit.paralleleditor.gui

import swing.event.Event

/**
 * Eventos de la GUI, generados desde los controles hacia las reactions que estan en la GUI
 */

case class ConnectionRequest(val host: String, val port: Int) extends Event
case class DisconnectionRequest extends Event

/**
 * Eventos publicados por
 */

abstract class EditionEvent extends Event
case class InsertionEvent(val pos: Int, val text: String) extends EditionEvent
case class DeletionEvent(val pos: Int, val count: Int) extends EditionEvent
