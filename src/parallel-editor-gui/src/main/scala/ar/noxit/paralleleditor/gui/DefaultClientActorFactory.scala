package ar.noxit.paralleleditor.gui

import java.net.Socket
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection
import remotes.RemoteServerProxy

class DefaultClientActorFactory extends ClientActorFactory {
    def newActor(host: String, port: Int, docs: Documents) = {
        val socket = new Socket(host, port)
        val factory = new InternalClientActorFactory(docs)

        // TODO resolver el tema de la conexión, que se cierra on disconnect
        new RemoteServerProxy(new SocketNetworkConnection(socket), factory)

        factory.clientActor
    }
}
