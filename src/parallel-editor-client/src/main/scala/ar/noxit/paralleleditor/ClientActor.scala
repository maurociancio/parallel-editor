package ar.noxit.paralleleditor.client

import ar.noxit.paralleleditor.common.remote.TerminateActor
import ar.noxit.paralleleditor.common.converter.MessageConverter
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import actors.{TIMEOUT, Actor}
import ar.noxit.paralleleditor.common.converter.MessageConverter

trait DocumentList {
    def changeDocList(l: List[String])
}

class ClientActor(private val doc: Documents) extends Actor with Loggable {
    val timeout = 5000
    var remoteKernelActor: Actor = _

    var converter: MessageConverter = _

    override def act = {
        trace("Waiting for remote kernel actor registration")

        // espero registracion
        remoteKernelActor = receiveWithin(timeout) {
            case RegisterRemoteActor(caller) => {
                trace("Remote kernel actor received, registered ok")
                caller
            }
            case TIMEOUT => doTimeout
        }

        loop {
            trace("Choosing")

            react {
                case r: RemoteLoginRequest => {
                    trace("Login request received")
                    remoteKernelActor ! ToKernel(r)
                }
                case s: RemoteSubscribeRequest => {
                    trace("RemoteSubscribeRequest")
                    remoteKernelActor ! ToKernel(s)
                }
                case e: RemoteDocumentListRequest => {
                    trace("RemoteDocumentListRequest")
                    remoteKernelActor ! ToKernel(e)
                }
                case e: RemoteNewDocumentRequest => {
                    trace("RemoteNewDocumentRequest")
                    remoteKernelActor ! ToKernel(e)
                }
                case e: RemoteUnsubscribeRequest => {
                    trace("RemoteUnsubscribeRequest")
                    remoteKernelActor ! ToKernel(e)
                }

                case o: RemoteDocumentOperation => {
                    if (sender != remoteKernelActor)
                        remoteKernelActor ! ToKernel(o)
                    else {
                        val m = converter.convert(o.payload)
                        doc.byName(o.docTitle).foreach {doc => doc processRemoteOperation m}
                    }
                }

                case RemoteDocumentSubscriptionResponse(docTitle, initialContent) => {
                    trace("RemoteDocumentSubscriptionResponse received")
                    doc.createDocument(docTitle, initialContent)
                }
                case RemoteDocumentListResponse(l) => {
                    trace("RemoteDocumentListResponse %s", l)
                    doc.changeDocList(l)
                }

                case r: RemoteLoginRefusedRemoteResponse => {
                    trace("login refused from kernel.")

                    r match {
                        case UsernameAlreadyExistsRemoteResponse() => {
                            trace("username already exists")
                            doc.usernameTaken
                        }
                    }
                }
                case RemoteLoginOkResponse() => {
                    trace("login accepted from kernel.")
                }

                case Logout() => {
                    trace("Logout request received")
                    doExit
                }

                case any: Any => {
                    warn("Uknown message received [%s]", any)
                }
            }
        }
    }

    private def doTimeout = {
        trace("timeout")
        doExit
    }

    private def doExit = {
        if (remoteKernelActor != null)
            remoteKernelActor ! TerminateActor()
        exit
    }
}
