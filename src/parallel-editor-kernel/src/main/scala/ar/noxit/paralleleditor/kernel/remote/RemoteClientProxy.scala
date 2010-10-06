package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.common.remote.{PeerActorFactory, BasePeerProxy}
import ar.noxit.paralleleditor.common.network._

class RemoteClientProxy(private val nc: NetworkConnection,
                        private val factory: PeerActorFactory,
                        private val callback: DisconnectablePeer)
        extends BasePeerProxy(nc, callback) {
    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newNetworkListener(input: MessageInput) = new NetworkListenerActor(input)

    protected def newClient() = factory.newClientActor(this)
}
