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
            while (!finished) {
                trace("waiting messages from docs")

                receiveWithin(timeout) {
                    case DocumentUserListResponse(docTitle, users) => {
                        users.foreach {
                            username => usernames = usernames.updated(username, docTitle :: usernames(username))
                        }
                        received = received + 1
                        finished = received == total
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
