package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import scala.actors._
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.kernel.{Session, DocumentSession}
import ar.noxit.paralleleditor.common.remote.{TerminateActor, NetworkActors, Peer}
import ar.noxit.paralleleditor.common.converter._
import ar.noxit.paralleleditor.common.operation.DocumentOperation

class ClientActor(private val kernel: Actor, private val client: Peer) extends Actor with Loggable {
    private var docSessions: List[DocumentSession] = List()
    private val timeout = 5000

    private var listener: Actor = _
    private var gateway: Actor = _
    private var session: Session = _

    override def act = {
        trace("Starting")

        // receive network actors
        val (gateway, listener) = receiveNetworkActors
        this.listener = listener
        this.gateway = gateway

        // receive username
        val username = receiveUsername

        // login to kernel and wait for response
        loginToKernel(username)
        session = receiveSession

        // notify client logged in
        notifyClientLoginOk

        // install callback
        installCallback

        processMessages
    }

    private def processMessages() {
        loop {
            react {
                case RemoteNewDocumentRequest(title) => {
                    trace("New Document Requested=[%s]", title)

                    kernel ! NewDocumentRequest(session, title)
                }

                case SubscriptionResponse(docSession, initialContent) => {
                    trace("Received Document Session")

                    docSessions = docSession :: docSessions
                    gateway ! RemoteDocumentSubscriptionResponse(docSession.title, initialContent)
                }

                case RemoteDocumentListRequest() => {
                    trace("Document List Requested")

                    kernel ! DocumentListRequest(session)
                }

                case DocumentListResponse(docList) => {
                    trace("Document List Response")

                    gateway ! RemoteDocumentListResponse(docList)
                }

                case RemoteSubscribeRequest(title) => {
                    trace("RemoteSubscribeRequest")
                    // FIX
                    kernel ! SubscribeToDocumentRequest(session, title)
                }

                case RemoteDocumentOperation(docTitle, payload) => {
                    trace("remove operation received")

                    val converter = new DefaultMessageConverter(new DefaultRemoteOperationConverter)
                    val message = converter.convert(payload)

                    docSessions.find {s => s.title == docTitle}.foreach {ds => ds applyChange message}
                }

                // estos mensajes vienen de los documentos y se deben propagar al cliente
                case PublishOperation(title, m) => {
                    trace("operation received from document")

                    // inyectar TODO
                    val converter = new DefaultRemoteDocumentOperationConverter(new DefaultSyncOperationConverter(new DefaultEditOperationConverter))
                    val converted = converter.convert(new DocumentOperation(title, m))
                    gateway ! converted
                }

                case TerminateActor() => {
                    trace("Exit received")
                    doExit
                }

                case RemoteLogoutRequest() => {
                    trace("Logout Requested")
                    doExit
                }

                case message: Any => {
                    trace("unkown message received %s", message)
                }
            }
        }
    }

    private def receiveNetworkActors = {
        trace("Waiting for actors")

        receiveWithin(timeout) {
            case NetworkActors(gateway, listener) => {
                trace("network actors received")
                (gateway, listener)
            }
            case TIMEOUT => doTimeout
        }
    }

    private def receiveUsername = {
        trace("Waiting for username")

        receiveWithin(timeout) {
            case RemoteLoginRequest(username) => {
                trace("Username received=[%s]", username)
                username
            }
            case TIMEOUT => doTimeout
        }
    }

    private def loginToKernel(username: String) = {
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
        gateway ! RemoteLoginOkResponse()
    }

    private def installCallback() {
        // install callback
        session.installOnUpdateCallback(new ActorCallback(this))
    }

    private def doTimeout = {
        trace("timeout")
        doExit
    }

    private def doExit = {
        warn("Client actor exiting")

        if (gateway != null)
            gateway ! TerminateActor()
        if (listener != null)
            listener ! TerminateActor()
        if (session != null)
            session.logout

        client.disconnect
        exit
    }
}
