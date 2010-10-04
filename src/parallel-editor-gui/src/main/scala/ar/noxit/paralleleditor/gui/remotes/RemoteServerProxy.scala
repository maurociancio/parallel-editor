package ar.noxit.paralleleditor.gui.remotes

import actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages.{RemoteDeleteText, RemoteAddText}
import ar.noxit.paralleleditor.common.network._
import concurrent.TIMEOUT
import ar.noxit.paralleleditor.common.remote.{Peer, NetworkActors, BasePeerProxy}

trait LocalClientActorFactory {
    def newLocalClientActor: Actor
}

object NullDisconnectablePeer extends DisconnectablePeer {
    def disconnect(peer: Peer) = {
    }
}

class RemoteServerProxy(private val networkConnection: NetworkConnection,
                        private val clientActorFactory: LocalClientActorFactory)
        extends BasePeerProxy(networkConnection, NullDisconnectablePeer) {

    protected def newNetworkListener(input: MessageInput) = new NetworkListenerActor(input) {
        override protected def onNewMessage(inputMessage: Any) = {
            peer ! ("from_kernel", inputMessage)
        }
    }

    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newClient() = new RemoteKernelActor(clientActorFactory)
}

class RemoteKernelActor(private val clientActorFactory: LocalClientActorFactory) extends Actor with Loggable {
    val localClientActor = clientActorFactory.newLocalClientActor.start
    val timeout = 5000

    private var listener: Actor = _
    private var gateway: Actor = _

    override def act = {
        trace("Sending registration to local client")

        val (gateway, listener) = receiveNetworkActors
        this.listener = listener
        this.gateway = gateway

        localClientActor ! ("registration", this)

        loop {
            react {
                case ("to_kernel", msg: Any) => {
                    trace("Received message to kernel %s", msg)
                    gateway ! msg
                }
                case ("from_kernel", msg: Any) => {
                    trace("Received message from kernel %s", msg)

                    msg match {
                        case at: RemoteAddText =>
                            localClientActor ! ("from_kernel", at)
                        case dt: RemoteDeleteText =>
                            localClientActor ! ("from_kernel", dt)
                        case _ =>
                            localClientActor ! msg
                    }
                }

                case "exit" => {
                    doExit
                }

                case any: Any =>
                    trace("Unknown message received %s", any)
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
            case TIMEOUT => doExit
        }
    }

    private def doExit = {
        gateway ! "exit"
        trace("Terminating")

        exit
    }
}
