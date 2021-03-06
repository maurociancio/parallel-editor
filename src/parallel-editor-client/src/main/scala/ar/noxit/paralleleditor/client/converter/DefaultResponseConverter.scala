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
package ar.noxit.paralleleditor.client.converter

import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor._
import client._
import common.logger.Loggable

class DefaultResponseConverter extends ResponseConverter with Loggable {
    override def convert(response: Response) = {
        response match {
            case RemoteDocumentSubscriptionResponse(docTitle, initialContent) => {
                trace("RemoteDocumentSubscriptionResponse received")
                DocumentSubscription(docTitle, initialContent)
            }
            case RemoteDocumentSubscriptionAlreadyExists(offenderTitle) => {
                trace("RemoteDocumentSubscriptionAlreadyExists")
                DocumentSubscriptionAlreadyExists(offenderTitle)
            }
            case RemoteDocumentSubscriptionNotExists(offenderTitle) => {
                trace("RemoteDocumentSubscriptionNotExists")
                DocumentSubscriptionNotExists(offenderTitle)
            }
            case RemoteDocumentInUse(docTitle) => {
                trace("RemoteDocumentInUse")
                DocumentInUse(docTitle)
            }
            case RemoteDocumentListResponse(l) => {
                trace("RemoteDocumentListResponse %s", l)
                DocumentListUpdate(l)
            }
            case RemoteUserListResponse(usernames) => {
                trace("RemoteUserListResponse")
                UserListUpdate(usernames)
            }

            case RemoteDocumentTitleExists(offenderTitle) => {
                trace("RemoteDocumentTitleExists %s", offenderTitle)
                DocumentTitleTaken(offenderTitle)
            }

            case r: RemoteLoginRefusedRemoteResponse => {
                trace("login refused from kernel.")

                r match {
                    case UsernameAlreadyExistsRemoteResponse() => {
                        trace("username already exists")
                        UsernameTaken()
                    }
                }
            }

            case RemoteDocumentDeletedOk(docTitle) => {
                trace("RemoteDocumentDeletedOk")
                DocumentDeleted(docTitle)
            }
            case RemoteDocumentDeletionTitleNotExists(docTitle) => {
                trace("RemoteDocumentDeletionTitleNotExists")
                DocumentDeletionTitleNotExists(docTitle)
            }

            case RemoteLoginOkResponse() => {
                trace("login accepted from kernel.")
                LoginOk()
            }

            case RemoteNewUserLoggedIn(username) =>
                NewUserLoggedIn(username)

            case RemoteUserLoggedOut(username) =>
                UserLoggedOut(username)

            case RemoteNewSubscriberToDocument(username, docTitle) =>
                NewSubscriberToDocument(username, docTitle)

            case RemoteSubscriberLeftDocument(username, docTitle) =>
                SubscriberLeftDocument(username, docTitle)

            case RemoteDocumentNotExists(offenderTitle) =>
                DocumentTitleNotExists(offenderTitle)

            case RemoteSubscriptionCancelled(docTitle) =>
                SubscriptionCancelled(docTitle)

            case RemoteChatMessage(username, message) =>
                ChatMessage(username, message)
        }
    }
}
