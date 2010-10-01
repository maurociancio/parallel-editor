package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.kernel.network.{MessageOutput, MessageInput, NetworkConnection}

class RemoteClientProxy(private val nc: NetworkConnection,
                        private val factory: ClientActorFactory,
                        private val callback: DisconnectClientCallback)
        extends BaseRemoteClientProxy(nc, callback) {

    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newNetworkListener(input: MessageInput) = new NetworkListenerActor(input)

    protected def newClient() = factory.newClientActor(this)
}
