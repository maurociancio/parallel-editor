package ar.noxit.paralleleditor.gui

import remotes.LocalClientActorFactory
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.remote.TerminateActor
import actors.{TIMEOUT, Actor}
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.converter.DefaultRemoteOperationConverter

trait ConcurrentDocument {
    def processOperation(o: EditOperation)
}

trait DocumentList {
    def changeDocList(l: List[String])
}

trait Documents {
    def byName(title: String): Option[ConcurrentDocument]

    def changeDocList(l: List[String])

    def createDocument(title: String, content: String)
}

class GuiActorFactory(private val doc: Documents) extends LocalClientActorFactory {
    val guiActor = new GuiActor(doc)

    override def newLocalClientActor = guiActor
}

class GuiActor(private val doc: Documents) extends Actor with Loggable {
    val timeout = 5000
    var remoteKernelActor: Actor = _
    // TODO INYECTAR
    val opConverter = new DefaultRemoteOperationConverter

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
                case Login(username) => {
                    trace("Login request received")
                    remoteKernelActor ! ToKernel(RemoteLoginRequest(username))
                }

                case Logout() => {
                    trace("Logout request received")
                    doExit
                }

                case o: RemoteOperation => {
                    if (sender != remoteKernelActor)
                        remoteKernelActor ! ToKernel(o)
                    else
                        doc.byName("new_document").foreach {doc => doc processOperation (opConverter convert o)}
                }
                case RemoteLoginRefusedResponse(reason) => {
                    trace("login refused from kernel. Reason: %s", reason)
                }
                case RemoteLoginOkResponse() => {
                    trace("login accepted from kernel.")
                }
                case RemoteDocumentSubscriptionResponse(initialContent) => {
                    trace("RemoteDocumentSubscriptionResponse received")
                    doc.createDocument("new_document", initialContent)
                }
                case e: RemoteDocumentListRequest => {
                    trace("RemoteDocumentListRequest")
                    remoteKernelActor ! ToKernel(e)
                }
                case RemoteDocumentListResponse(l) => {
                    trace("RemoteDocumentListResponse %s", l)
                    doc.changeDocList(l)
                }
                case s: RemoteSubscribeRequest => {
                    trace("RemoteSubscribeRequest")
                    remoteKernelActor ! ToKernel(s)
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
