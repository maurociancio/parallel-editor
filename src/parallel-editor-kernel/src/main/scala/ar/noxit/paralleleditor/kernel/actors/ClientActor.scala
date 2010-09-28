package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.DocumentSession
import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import scala.actors._
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.messages._

class ClientActor(private val kernel: Actor, private val remoteClient: Actor) extends Actor with Loggable {
    var docSessions: List[DocumentSession] = List()
    val timeout = 5000

    def act = {
        trace("Waiting for username")

        val username = receiveWithin(timeout) {
            case RemoteLogin(username) => username
            case TIMEOUT => doTimeout
        }
        trace("Username received=[%s]", username)

        trace("Sending login request")
        // logging into the kernel
        kernel ! LoginRequest(username)

        trace("Waiting for session")
        // we expect a session in order to continue
        val session = receiveWithin(timeout) {
            case LoginResponse(session) => {
                trace("Session received")
                session
            }
            case TIMEOUT => doTimeout
        }
        // notify the remote target
        remoteClient ! RemoteLoginOkResponse()

        // install callback
        session.installOnUpdateCallback(new ActorCallback(this))

        var exit = false
        loopWhile(!exit) {
            react {
                case RemoteNewDocumentRequest(title) => {
                    trace("New Document Requested=[%s]", title)

                    kernel ! NewDocumentRequest(session, title)
                }

                case NewDocumentResponse(docSession) => {
                    trace("Received Document Session")

                    docSessions = docSession :: docSessions
                    remoteClient ! RemoteNewDocumentOkResponse
                }

                case RemoteDocumentList => {
                    trace("Document List Requested")

                    kernel ! DocumentListRequest(session)
                }

                case DocumentListResponse(docList) => {
                    trace("Document List Response")
                    remoteClient ! RemoteDocumentListResponse(docList)
                }

                case "EXIT" => {
                    trace("Exit received")
                    remoteClient ! "EXIT" // TODO

                    session.logout
                    exit = true
                }

                case RemoteLogoutRequest => {
                    trace("Logout Requested")
                    session.logout

                    exit = true
                }
            }
        }
    }

    def doTimeout = {
        warn("Timeout waiting for a message")
        throw new IllegalStateException("time out")
    }
}
