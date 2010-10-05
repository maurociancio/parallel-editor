package ar.noxit.paralleleditor.gui

import remotes.LocalClientActorFactory
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.remote.TerminateActor
import actors.{TIMEOUT, Actor}

class GuiActorFactory(private val doc: ConcurrentDocument) extends LocalClientActorFactory {

    val guiActor = new GuiActor(doc)

    override def newLocalClientActor = guiActor
}

class GuiActor(private val doc: ConcurrentDocument) extends Actor with Loggable {

    val timeout = 5000
    var remoteKernelActor: Actor = _

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
                        o match {
                            case addText: RemoteAddText => {
                                doc.addText(addText.startPos, addText.text)
                            }
                            case deleteText: RemoteDeleteText => {
                                doc.removeText(deleteText.startPos, deleteText.size)
                            }
                            case _ => warn("unkown remote operation")
                        }
                }

                case RemoteLoginRefusedResponse(reason) => {
                    trace("login refused from kernel. Reason: %s", reason)
                }

                case RemoteLoginOkResponse() => {
                    trace("login accepted from kernel.")
                }

                case (RemoteDocumentSubscriptionResponse(initialContent)) => {
                    trace("RemoteDocumentSubscriptionResponse received")
                    doc.initialContent(initialContent)
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
