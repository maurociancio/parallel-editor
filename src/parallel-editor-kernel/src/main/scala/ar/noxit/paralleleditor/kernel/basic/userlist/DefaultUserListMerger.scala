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
package ar.noxit.paralleleditor.kernel.basic.userlist

import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.kernel.basic.{DocumentActor, UserListMerger}
import scala.actors.Actor.{actor, receiveWithin}
import ar.noxit.paralleleditor.kernel.messages.{UserListResponse, DocumentUserListRequest, DocumentUserListResponse}
import actors.TIMEOUT
import ar.noxit.paralleleditor.common.logger.Loggable
import reflect.BeanProperty

class DefaultUserListMerger extends UserListMerger with Loggable {

    @BeanProperty
    var timeout: Int = _

    override def notifyUserList(to: Session, sessions: List[Session], docs: List[DocumentActor]) = {
        asyncRun {
            var usernames = sessions.map {s => (s.username, List[String]())}.toMap

            trace("count started")
            docs.foreach {doc => doc ! DocumentUserListRequest()}

            var received = 0
            val total = docs.size

            var finished = false
            while (received < total && !finished) {
                trace("waiting messages from docs")

                receiveWithin(timeout) {
                    case DocumentUserListResponse(docTitle, users) => {
                        users.foreach {
                            username => usernames = usernames.updated(username, docTitle :: usernames(username))
                        }
                        received = received + 1
                    }
                    case TIMEOUT => {
                        warn("document did not respond to user list req")
                        finished = true
                    }
                }
            }

            trace("notifying user list to client")
            to notifyUpdate UserListResponse(usernames)
        }
    }

    protected def asyncRun(closure: => Unit) = {
        actor {
            closure
        }
    }
}
