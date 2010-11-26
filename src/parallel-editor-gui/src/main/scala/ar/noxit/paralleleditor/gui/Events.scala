/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.gui

import swing.event.Event
import ar.noxit.paralleleditor.common.operation.DocumentOperation

/**
 * Eventos de la GUI, generados desde los controles hacia las reactions que estan en la GUI
 */

case class ConnectionRequest(val host: String, val port: Int) extends Event
case class DisconnectionRequest() extends Event
case class DocumentListRequest() extends Event
case class UserListRequest() extends Event
case class NewDocumentRequest(val docTitle: String, val initialContent: String = "") extends Event
case class CloseCurrentDocument() extends Event
case class DeleteCurrentDocument() extends Event
case class ExitRequested() extends Event
case class SaveCurrentDocumentRequest() extends Event

/**
 * Eventos publicados por el document area
 */

/**
 * Operacion de edicion sobre un documento
 */
trait EditionEvent extends Event
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
