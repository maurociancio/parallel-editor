package ar.noxit.paralleleditor.gui

import remotes.LocalClientActorFactory
import actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._

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
            case ("registration", caller: Actor) => {
                trace("Remote kernel actor received, registered ok")
                caller
            }
            // TODO timeout
        }

        var exit = false
        loopWhile(!exit) {
            trace("Choosing")

            react {
                case Login(username) => {
                    trace("Login request received")
                    remoteKernelActor ! ("to_kernel", RemoteLoginRequest(username))
                }

                case "logout" => {
                    trace("Logout request received")
                    remoteKernelActor ! ("to_kernel", RemoteLogoutRequest())
                    remoteKernelActor ! "exit"
                    exit = true
                }

                case "exit" => {
                    trace("Logout request received")
                    remoteKernelActor ! ("to_kernel", RemoteLogoutRequest())
                }

                case addText: RemoteAddText => {
                    remoteKernelActor ! ("to_kernel", addText)
                }

                case deleteText: RemoteDeleteText => {
                    remoteKernelActor ! ("to_kernel", deleteText)
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

                case ("from_kernel", addText: RemoteAddText) => {
                    trace("received from kernel add text")
                    doc.addText(addText.startPos, addText.text)
                }

                case ("from_kernel", deleteText: RemoteDeleteText) => {
                    trace("received from kernel delete text")
                    doc.removeText(deleteText.startPos, deleteText.size)
                }

                case any: Any => {
                    warn("Uknown message received [%s]", any)
                }
            }
        }
    }
}
