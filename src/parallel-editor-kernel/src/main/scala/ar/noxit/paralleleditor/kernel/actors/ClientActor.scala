package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import scala.actors._
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.kernel.remote.Client
import ar.noxit.paralleleditor.kernel.{EditOperation, Session, DocumentSession}
import ar.noxit.paralleleditor.kernel.operations._

class ClientActor(private val kernel: Actor, private val client: Client) extends Actor with Loggable {
    private var docSessions: List[DocumentSession] = List()
    private val timeout = 5000

    private var inputActor: Actor = _
    private var gatewayActor: Actor = _

    override def act = {
        trace("Starting")
        receiveActors

        val username = receiveUsername
        sendLoginToKernel(username)

        val session = receiveSession
        notifyClientLoginOk

        installCallback(session)

        // TODO
        kernel ! SubscribeToDocument(session, "new_document")

        processMessages(session)
    }

    private def processMessages(session: Session) {
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
                    gatewayActor ! RemoteNewDocumentOkResponse() // MAL
                }

                case RemoteDocumentList => {
                    trace("Document List Requested")

                    kernel ! DocumentListRequest(session)
                }

                case DocumentListResponse(docList) => {
                    trace("Document List Response")

                    gatewayActor ! RemoteDocumentListResponse(docList)
                }

                case DeleteText(startPos, count) => {
                    trace("delete text received")

                    docSessions.foreach(session => session applyChange (new DeleteTextOperation(startPos, count)))
                }

                case AddText(text, startPos) => {
                    trace("addtext text received")
                    docSessions.foreach(session => session applyChange (new AddTextOperation(text, startPos)))
                }

                // estos mensajes vienen de los documentos y se deben propagar al cliente
                case e: EditOperation => {
                    trace("operation received from document")

                    e match {
                        case o: AddTextOperation =>
                            gatewayActor ! AddText(o.text, o.startPos)
                        case o: DeleteTextOperation =>
                            gatewayActor ! DeleteText(o.startPos, o.size)
                    }
                }

                case "EXIT" => {
                    trace("Exit received")
                    logoutFromKernel(session)
                    client.disconnect
                    exit = true
                }

                case RemoteLogoutRequest => {
                    trace("Logout Requested")
                    logoutFromKernel(session)
                    client.disconnect
                    exit = true
                }

                case message: Any => {
                    trace("unkown message received %s", message)
                }
            }
        }
    }

    private def receiveActors {
        trace("Waiting for actors")

        while (!(inputActor != null && gatewayActor != null)) {
            receiveWithin(timeout) {

                case ("input", input: Actor) if inputActor == null => {
                    trace("Received input actor")
                    inputActor = input
                }
                case ("gateway", gateway: Actor) if gatewayActor == null => {
                    trace("Received gateway actor")
                    gatewayActor = gateway
                }

                case TIMEOUT => doTimeout
            }
        }
    }

    private def receiveUsername = {
        trace("Waiting for username")

        val username = receiveWithin(timeout) {
            case RemoteLogin(username) => username
            case TIMEOUT => doTimeout
        }

        trace("Username received=[%s]", username)

        username
    }

    private def sendLoginToKernel(username: String) = {
        trace("Sending login request")

        // logging into the kernel
        kernel ! LoginRequest(username)
    }

    private def receiveSession = {
        trace("Waiting for session")

        // we expect a session in order to continue
        receiveWithin(timeout) {
            case LoginResponse(session) => {
                trace("Session received")
                session
            }
            case TIMEOUT => doTimeout
        }
    }

    private def notifyClientLoginOk {
        // notify the remote target
        gatewayActor ! RemoteLoginOkResponse()
    }

    private def installCallback(session: Session) {
        // install callback
        session.installOnUpdateCallback(new ActorCallback(this))
    }

    private def logoutFromKernel(session: Session) {
        gatewayActor ! "EXIT" // TODO
        inputActor ! "EXIT" // TODO

        session.logout
    }

    private def doTimeout = {
        warn("Timeout waiting for a message")
        // TODO mandar mensajes para que terminen los demas actores
        // TODO finalizar el finalizable
        throw new IllegalStateException("time out")
    }
}
