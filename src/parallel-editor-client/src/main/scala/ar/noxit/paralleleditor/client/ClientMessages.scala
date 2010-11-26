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
package ar.noxit.paralleleditor.client

import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation

/**
 * Comando desde el kernel
 */
sealed trait CommandFromKernel

case class ProcessOperation(val title: String, val msg: Message[EditOperation]) extends CommandFromKernel

case class DocumentListUpdate(val docs: List[String]) extends CommandFromKernel

case class UserListUpdate(val usernames: Map[String, List[String]]) extends CommandFromKernel

case class DocumentSubscription(val title: String, val initialContent: String) extends CommandFromKernel

case class DocumentSubscriptionAlreadyExists(val offenderTitle: String) extends CommandFromKernel

case class DocumentSubscriptionNotExists(val offenderTitle: String) extends CommandFromKernel

case class DocumentInUse(val docTitle: String) extends CommandFromKernel

case class UsernameTaken() extends CommandFromKernel

case class DocumentDeleted(val docTitle: String) extends CommandFromKernel

case class DocumentDeletionTitleNotExists(val docTitle: String) extends CommandFromKernel

case class DocumentTitleTaken(val offenderTitle: String) extends CommandFromKernel

case class LoginOk() extends CommandFromKernel

case class NewUserLoggedIn(val username: String) extends CommandFromKernel

case class UserLoggedOut(val username: String) extends CommandFromKernel

case class NewSubscriberToDocument(val username: String, val docTitle: String) extends CommandFromKernel

case class SubscriberLeftDocument(val username: String, val docTitle: String) extends CommandFromKernel

case class DocumentTitleNotExists(val offenderTitle: String) extends CommandFromKernel

case class SubscriptionCancelled(val docTitle: String) extends CommandFromKernel

case class ChatMessage(val username: String, val message: String) extends CommandFromKernel
