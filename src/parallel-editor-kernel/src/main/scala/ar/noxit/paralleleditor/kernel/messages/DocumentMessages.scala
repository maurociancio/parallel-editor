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
package ar.noxit.paralleleditor.kernel.messages

import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.Message

/**
 * Mensajes que se envian entre el actor del documento y el kernel.
 */

case class TerminateDocument()

case class ProcessOperation(val who: Session, val m: Message[EditOperation])

case class SubscriberCount()
case class Subscribe(val who: Session)
case class Unsubscribe(val who: Session)
case class SilentUnsubscribe(val session: Session)
case class Close(val session: Session)
case class DocumentDeleted(val docTitle: String)

case class DocumentUserListRequest()

case class DocumentUserListResponse(val docTitle: String, val users: List[String])

/**
 * La envia el doc actor para que el client actor la retransmita al cliente remoto
 */
case class PublishOperation(val docTitle: String, val m: Message[EditOperation])
