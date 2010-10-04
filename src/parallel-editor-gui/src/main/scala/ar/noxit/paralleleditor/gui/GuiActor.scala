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
        receiveWithin(timeout) {
            case ("registration", caller: Actor) => {
                trace("Remote kernel actor received, registered ok")

                remoteKernelActor = caller
                remoteKernelActor ! "registration_ok"
            }
            // TODO timeout
        }

        var exit = false
        loopWhile(!exit) {
            trace("Choosing")

            react {
                case ("login", username: String) => {
                    trace("Login request received")
                    remoteKernelActor ! ("to_kernel", RemoteLogin(username))
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

                case addText: AddText => {
                    remoteKernelActor ! ("to_kernel", addText)
                }

                case deleteText: DeleteText => {
                    remoteKernelActor ! ("to_kernel", deleteText)
                }


                case RemoteLoginRefusedResponse(reason) => {
                    trace("login refused from kernel. Reason: %s",reason)
                }

                case RemoteLoginOkResponse() => {
                    trace("login accepted from kernel.")
                }

                case ("from_kernel", addText: AddText) => {
                    trace("received from kernel add text")
                    doc.addText(addText.startPos, addText.text)
                }
                
                case ("from_kernel", deleteText: DeleteText) => {
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
