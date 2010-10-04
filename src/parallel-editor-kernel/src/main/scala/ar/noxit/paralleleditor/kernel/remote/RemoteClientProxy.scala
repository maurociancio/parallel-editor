package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.common.remote.{ClientActorFactory, BasePeerProxy}
import ar.noxit.paralleleditor.common.network.{DisconnectClientCallback, MessageOutput, MessageInput, NetworkConnection}

class RemoteClientProxy(private val nc: NetworkConnection,
                        private val factory: ClientActorFactory,
                        private val callback: DisconnectClientCallback)
        extends BasePeerProxy(nc, callback) {
    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newNetworkListener(input: MessageInput) = new NetworkListenerActor(input)

    protected def newClient() = factory.newClientActor(this)
}
