package ar.noxit.paralleleditor.client

import ar.noxit.paralleleditor.common.remote.TerminateActor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import actors.{TIMEOUT, Actor}
import ar.noxit.paralleleditor.common.converter.MessageConverter
import reflect.BeanProperty
import ar.noxit.paralleleditor._

class ClientActor(private val doc: Documents) extends Actor with Loggable {
    @BeanProperty
    var timeout = 5000
    @BeanProperty
    var converter: MessageConverter = _

    private var remoteKernelActor: Actor = _

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
                        doc.process(ProcessOperation(o.docTitle, m))
                    }
                }

                case RemoteDocumentSubscriptionResponse(docTitle, initialContent) => {
                    trace("RemoteDocumentSubscriptionResponse received")
                    doc.process(DocumentSubscription(docTitle, initialContent))
                }
                case RemoteDocumentSubscriptionAlreadyExists(offenderTitle) => {
                    trace("RemoteDocumentSubscriptionAlreadyExists")
                    doc.process(DocumentSubscriptionAlreadyExists(offenderTitle))
                }
                case RemoteDocumentSubscriptionNotExists(offenderTitle) => {
                    trace("RemoteDocumentSubscriptionNotExists")
                    doc.process(DocumentSubscriptionNotExists(offenderTitle))
                }
                case RemoteDocumentListResponse(l) => {
                    trace("RemoteDocumentListResponse %s", l)
                    doc.process(DocumentListUpdate(l))
                }
                case RemoteDocumentTitleExists(offenderTitle) => {
                    trace("RemoteDocumentTitleExists %s", offenderTitle)
                    doc.process(DocumentTitleTaken(offenderTitle))
                }

                case r: RemoteLoginRefusedRemoteResponse => {
                    trace("login refused from kernel.")

                    // usar un converter
                    r match {
                        case UsernameAlreadyExistsRemoteResponse() => {
                            trace("username already exists")
                            doc.process(UsernameTaken())
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
