package ar.noxit.paralleleditor.gui.remotes

import java.net.Socket
import actors.Actor
import java.io.{ObjectInputStream, ObjectInput, ObjectOutputStream, ObjectOutput}
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages.{DeleteText, AddText}

trait LocalClientActorFactory {
    def newLocalClientActor: Actor
}

class RemoteServerProxy(private val socket: Socket, private val clientActorFactory: LocalClientActorFactory) {
    val gateway = new GatewayActor(new ObjectOutputStream(socket.getOutputStream)).start
    val remoteKernel = new RemoteKernelActor(gateway, clientActorFactory).start
    val networkListener = new NetworkListenerActor(new ObjectInputStream(socket.getInputStream), remoteKernel).start
}

class GatewayActor(private val output: ObjectOutput) extends Actor with Loggable {
    override def act = {
        // TODO definir mensaje de salida
        var exit = false

        loopWhile(!exit) {
            react {
                case message: Any => {
                    trace("Sending message %s", message)
                    output writeObject message
                }
            }
        }
    }
}

class RemoteKernelActor(private val gateway: Actor, clientActorFactory: LocalClientActorFactory) extends Actor with Loggable {
    val localClientActor = clientActorFactory.newLocalClientActor.start
    val timeout = 5000

    override def act = {
        trace("Sending registration to local client")
        localClientActor ! ("registration", this)

        receiveWithin(timeout) {
            case "registration_ok" => trace("Registration ok")
            // TODO timeout
        }

        var exit = false
        loopWhile(!exit) {
            react {
                case ("to_kernel", msg: Any) => {
                    trace("Received message to kernel %s", msg)
                    gateway ! msg
                }
                case ("from_kernel", msg: Any) => {
                    trace("Received message from kernel %s", msg)

                    msg match {
                        case at: AddText =>
                            localClientActor ! ("from_kernel", at)
                        case dt: DeleteText =>
                            localClientActor ! ("from_kernel", dt)
                        case _ =>
                            localClientActor ! msg
                    }
                }
                case any: Any =>
                    trace("Unknown message received %s", any)
            }
        }
    }
}

class NetworkListenerActor(private val input: ObjectInput, private val remoteKernel: Actor) extends Actor with Loggable {
    override def act = {
        // TODO definir mensaje de salida
        var exit = false
        while (!exit) {
            val inputMessage: Any = input.readObject()
            trace("Received message %s", inputMessage)

            remoteKernel ! ("from_kernel", inputMessage)
        }
    }
}
