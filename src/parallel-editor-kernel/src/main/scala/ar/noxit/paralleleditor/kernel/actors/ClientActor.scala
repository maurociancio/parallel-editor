package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.DocumentSession
import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import ar.noxit.paralleleditor.kernel.Session
import scala.actors._
import ar.noxit.paralleleditor.kernel.messages._

class ClientActor(val kernel: Actor, val remoteClient: Actor) extends Actor {

    var docSessions: List[DocumentSession] = List()
    val timeout = 5000

    def act = {
        println("waiting for username")
        val username = receiveWithin(timeout) {
            case RemoteLogin(username) => username
            case TIMEOUT => doTimeout
        }
        println("username received " + username)

        // logging into the kernel
        kernel ! LoginRequest(username, this)

        // we expect a session in order to continue
        val session = receiveWithin(timeout) {
            case LoginResponse(session) => {
                println("session received")
                // return session
                session
            }
            case TIMEOUT => doTimeout
        }
        println(session)

        // notify the remote client
        remoteClient ! RemoteLoginOkResponse

        // install callback
        session.installOnUpdateCallback(new ActorCallback(this))

        var exit = false
        loopWhile(!exit) {
            react {
                case RemoteNewDocumentRequest(title) => {
                    println("new doc requested " + title)

                    kernel ! NewDocumentRequest(session, title)
                    receiveWithin(timeout) {
                        case NewDocumentResponse(docSession) => {
                            println("received doc session")

                            docSessions = docSession :: docSessions
                            remoteClient ! RemoteNewDocumentOkResponse
                        }
                        case TIMEOUT => doTimeout
                    }
                }

                case RemoteDocumentList => {
                    println("doclist requested")

                    kernel ! DocumentListRequest(session)
                }

                case DocumentListResponse(docList) => {
                    remoteClient ! RemoteDocumentListResponse(docList)
                }

                case RemoteLogoutRequest => {
                    println("logout requested")
                    session.logout

                    exit = true
                }
            }
        }
    }

    def doTimeout = throw new IllegalStateException("time out")
}