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

import ar.noxit.paralleleditor.kernel.{DocumentSession, Session}

/**
 * Se aplica a los mensajes que son convertibles hacia mensajes remotos
 */
sealed trait ToRemote

/**
 * Mensajes entre el actor Kernel y el actor del proxy del Cliente remoto
 */

case class TerminateKernel()

case class LoginRequest(val username: String)

case class LoginResponse(val session: Session) {
    if (session == null)
        throw new IllegalArgumentException("session cannot be null")
}

case class UsernameAlreadyExists()

case class DocumentTitleExists(val offenderTitle: String) extends ToRemote

case class DocumentDeletionTitleNotExists(val offenderTitle: String) extends ToRemote

case class DocumentNotExists(val offenderTitle: String) extends ToRemote

case class NewDocumentRequest(val session: Session, val title: String, val initialContent: String)

case class CloseDocument(val session: Session, val docTitle: String)

case class UserListRequest(val session: Session)

case class UserListResponse(val usernames: Map[String, List[String]]) extends ToRemote

/**
 * Generado cuando la suscripción a un documento fue exitosa.
 * Puede ser enviado tanto si es para un nuevo documento como para uno existente
 */
case class SubscriptionResponse(val docSession: DocumentSession, val initialContent: String) extends ToRemote

/**
 * Generado cuando una sesión ya está suscripta a un mensaje
 */
case class SubscriptionAlreadyExists(val offenderTitle: String) extends ToRemote

/**
 * Generado cuando se solicita desuscripción a un documento no suscripto
 */
case class SubscriptionNotExists(val offenderTitle: String) extends ToRemote

case class DocumentDeletedOk(val docTitle: String) extends ToRemote

case class DocumentInUse(val docTitle: String) extends ToRemote

case class DocumentListRequest(val session: Session)
case class DocumentListResponse(val documents: List[String]) extends ToRemote

case class SubscribeToDocumentRequest(val session: Session, val title: String)
case class UnsubscribeToDocumentRequest(val session: Session, val title: String)

/**
 * Generado cuando un usuario se loguea al kernel
 */
case class NewUserLoggedIn(val username: String) extends ToRemote

/**
 * Generado cuando un usuario se desloguea
 */
case class UserLoggedOut(val username: String) extends ToRemote

/**
 * Generado cuando un usuario se une a un documento
 */
case class NewSubscriberToDocument(val username: String, val docTitle: String) extends ToRemote

/**
 * Generado cuando un usuario deja la edición de un documento
 */
case class SubscriberLeftDocument(val username: String, val docTitle: String) extends ToRemote

/**
 * Generado cuando el usuario se desuscribe de un document
 */
case class SubscriptionCancelled(val docTitle: String) extends ToRemote

/**
 * Generado cuando un usuario envia un mensaje de chat
 */
case class ChatMessage(val session: Session, val message: String) extends ToRemote
