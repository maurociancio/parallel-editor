package ar.noxit.paralleleditor.gui.remotes

import actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.network._
import concurrent.TIMEOUT
import ar.noxit.paralleleditor.common.remote.{TerminateActor, Peer, NetworkActors, BasePeerProxy}
import ar.noxit.paralleleditor.common.messages.RemoteLogoutRequest
import ar.noxit.paralleleditor.gui.{RegisterRemoteActor, FromKernel, ToKernel}

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
            peer ! FromKernel(inputMessage)
        }
    }

    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newClient() = new RemoteKernelActor(clientActorFactory, this)
}

class RemoteKernelActor(private val clientActorFactory: LocalClientActorFactory,
                        private val peer: Peer) extends Actor with Loggable {

    val localClientActor = clientActorFactory.newLocalClientActor.start
    val timeout = 5000

    private var listener: Actor = _
    private var gateway: Actor = _

    override def act = {
        trace("Sending registration to local client")

        val (gateway, listener) = receiveNetworkActors
        this.listener = listener
        this.gateway = gateway

        localClientActor ! RegisterRemoteActor(this)

        loop {
            react {
                case ToKernel(msg) => {
                    trace("Received message to kernel %s", msg)
                    gateway ! msg
                }
                case FromKernel(msg) => {
                    trace("Received message from kernel %s", msg)
                    localClientActor ! msg
                }
                case TerminateActor() => {
                    gateway ! RemoteLogoutRequest()
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
        trace("Terminating")

        gateway ! TerminateActor()
        listener ! TerminateActor()
        peer.disconnect

        exit
    }
}
