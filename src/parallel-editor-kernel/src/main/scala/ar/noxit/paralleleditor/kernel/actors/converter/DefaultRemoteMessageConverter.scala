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
package ar.noxit.paralleleditor.kernel.actors.converter

import ar.noxit.paralleleditor.kernel.actors.RemoteMessageConverter
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.messages._

class DefaultRemoteMessageConverter extends RemoteMessageConverter {
    override def convert(remote: ToRemote) = {
        remote match {
            case SubscriptionAlreadyExists(offenderTitle) =>
                RemoteDocumentSubscriptionAlreadyExists(offenderTitle)

            case SubscriptionNotExists(offenderTitle) =>
                RemoteDocumentSubscriptionNotExists(offenderTitle)

            case DocumentListResponse(docList) =>
                RemoteDocumentListResponse(docList)

            case SubscriptionResponse(docSession, initialContent) =>
                RemoteDocumentSubscriptionResponse(docSession.title, initialContent)

            case DocumentTitleExists(offenderTitle) =>
                RemoteDocumentTitleExists(offenderTitle)

            case DocumentInUse(docTitle) =>
                RemoteDocumentInUse(docTitle)

            case DocumentDeletedOk(docTitle) =>
                RemoteDocumentDeletedOk(docTitle)

            case DocumentDeletionTitleNotExists(docTitle) =>
                RemoteDocumentDeletionTitleNotExists(docTitle)

            case UserListResponse(usernames) =>
                RemoteUserListResponse(usernames)

            case NewUserLoggedIn(username) =>
                RemoteNewUserLoggedIn(username)

            case UserLoggedOut(username) =>
                RemoteUserLoggedOut(username)

            case NewSubscriberToDocument(username, docTitle) =>
                RemoteNewSubscriberToDocument(username, docTitle)

            case SubscriberLeftDocument(username, docTitle) =>
                RemoteSubscriberLeftDocument(username, docTitle)

            case DocumentNotExists(offenderTitle) =>
                RemoteDocumentNotExists(offenderTitle)

            case SubscriptionCancelled(docTitle) =>
                RemoteSubscriptionCancelled(docTitle)

            case ChatMessage(session, message) =>
                RemoteChatMessage(session.username, message)
        }
    }
}
