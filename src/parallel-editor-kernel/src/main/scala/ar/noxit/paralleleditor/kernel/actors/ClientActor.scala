package ar.noxit.paralleleditor.kernel.actors

import scala.actors._
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.kernel.{Session, DocumentSession}
import ar.noxit.paralleleditor.common.converter._
import reflect.BeanProperty
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import ar.noxit.paralleleditor.common.remote.{NetworkActors, TerminateActor, Peer}
import ar.noxit.paralleleditor.common.operation.DocumentOperation
import ar.noxit.paralleleditor.common.network.SenderActor

class ClientActor(private val kernel: Actor, private val client: Peer) extends Actor with Loggable {
    @BeanProperty
    var timeout = 5000
    @BeanProperty
    var maxLoginTries = 5
    @BeanProperty
    var remoteDocOpconverter: RemoteDocumentOperationConverter = _
    @BeanProperty
    var messageConverter: MessageConverter = _
    @BeanProperty
    var remoteConverter: RemoteMessageConverter = _
    @BeanProperty
    var toKernelConverter: ToKernelConverter = _

    private var docSessions: List[DocumentSession] = List()

    private var listener: SenderActor = _
    private var gateway: SenderActor = _
    private var _session: Session = _

    override def act = {
        trace("Starting")

        // receive network actors
        val (gateway, listener) = receiveNetworkActors
        this.listener = listener
        this.gateway = gateway

        // wait for session
        _session = this.receiveSession(loginTries = 0)

        // process messages
        processMessages
    }

    protected def processMessages() {
        loop {
            react {
                // suscripcion
                case sr: SubscriptionResponse => {
                    trace("Received Document Session")

                    docSessions = sr.docSession :: docSessions
                    gateway ! remoteConverter.convert(sr)
                }

                // unsubscribe
                case RemoteUnsubscribeRequest(title) => {
                    trace("RemoteUnsubscribeRequest")

                    docSessions.find {s => s.title == title}.foreach {docSession => docSession.unsubscribe}
                    docSessions = docSessions.filter {docSession => docSession.title != title}
                }

                // estos mensajes vienen de los documentos y se deben propagar al cliente
                case PublishOperation(title, m) => {
                    trace("operation received from document")

                    gateway ! remoteDocOpconverter.convert(new DocumentOperation(title, m))
                }

                // operaciones al documento
                case RemoteDocumentOperation(docTitle, payload) => {
                    trace("remove operation received")

                    val message = messageConverter.convert(payload)
                    docSessions.find {s => s.title == docTitle}.foreach {ds => ds applyChange message}
                }

                // hacia gateway
                case convertible: ToRemote => {
                    trace("convertible %s", convertible)
                    gateway ! remoteConverter.convert(convertible)
                }

                // hacia kernel
                case tokernel: ToKernel => {
                    trace("to kernel %s", tokernel)
                    kernel ! toKernelConverter.convert(_session, tokernel)
                }

                case dd: DocumentDeleted => {
                    kernel ! dd
                }

                // control del actor
                case TerminateActor() => {
                    trace("Exit received")
                    doExit
                }

                case RemoteLogoutRequest() => {
                    trace("Logout Requested")
                    doExit
                }

                case message: Any => {
                    warn("unkown message received %s", message)
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

    private def receiveSession(loginTries: Int = 0): Session = {
        trace("Waiting for username")

        if (loginTries >= maxLoginTries) {
            warn("max login tries reached")
            doTimeout
        }

        // receive username
        val username = receiveWithin(timeout) {
            case RemoteLoginRequest(username) => {
                trace("Username received=[%s]", username)
                username
            }
            case TIMEOUT => doTimeout
        }

        // login to kernel and wait for response
        trace("Sending login request")

        // logging into the kernel
        kernel ! LoginRequest(username)

        trace("Waiting for session")

        // we expect a session in order to continue
        receiveWithin(timeout) {
            case LoginResponse(session) => {
                trace("Session received")

                // notify client logged in
                gateway ! RemoteLoginOkResponse()

                // install callback
                installCallback(session)

                session
            }
            case UsernameAlreadyExists() => {
                trace("username already exists")

                gateway ! UsernameAlreadyExistsRemoteResponse()
                receiveSession(loginTries + 1)
            }
            case TIMEOUT => doTimeout
        }
    }

    protected def installCallback(session: Session) {
        // install callback
        session.installOnUpdateCallback(new ActorCallback(this))
    }

    private def doTimeout = {
        trace("timeout")
        doExit
    }

    private def doExit = {
        warn("Client actor exiting")

        if (_session != null)
            _session.logout
        if (gateway != null)
            gateway ! TerminateActor()
        if (listener != null)
            listener ! TerminateActor()

        client.disconnect
        exit
    }

    def session = _session
}
