package ar.noxit.paralleleditor.gui

import remotes.LocalClientActorFactory
import actors.Actor
import ar.noxit.paralleleditor.common.messages.RemoteLogin
import ar.noxit.paralleleditor.common.logger.Loggable

class GuiActorFactory extends LocalClientActorFactory {
    val guiActor = new GuiActor
    override def newLocalClientActor = guiActor
}

class GuiActor extends Actor with Loggable {
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

                case ("insertion",pos:Integer,text:String) => {
                    remoteKernelActor ! ("to_kernel", "insert operation required")
                }

                case ("deletion",pos:Integer,count:Integer) => {
                    remoteKernelActor ! ("to_kernel", "delete operation required" )
                }
                
                case any: Any => {
                    warn("Uknown message received [%s]", any)
                }
            }
        }
    }
}
