package ar.noxit.paralleleditor.common.remote

import actors.Actor
import ar.noxit.paralleleditor.common.network.{DisconnectablePeer, NetworkConnection, MessageOutput, MessageInput}

trait Peer {
    def disconnect
}

trait PeerActorFactory {
    def newClientActor(peer: Peer): Actor
}

/**
 * Mensaje que transporta los actores gateway y listener hacia el actor peer
 */
case class NetworkActors(val gateway: Actor, val listener: Actor)

/**
 * Setea el actor peer a los actores listener y gateway
 */
case class SetPeerActor(val peer: Actor)

/**
 * Indica al actor que debe terminar su procesamiento. Enviado a los actores peer, gateway y listener
 */
case class TerminateActor

/**
 * Esta clase encapsula la referencia a un cliente remoto proveyendo una interfaz para definir el destinatario
 * de los mensajes que recibirán por la red y permite obtener un actor al cual enviarle mensajes para que lleguen
 * al cliente.
 */
abstract class BasePeerProxy(private val networkConnection: NetworkConnection,
                             private val disconnectCallback: DisconnectablePeer) extends Peer {

    /**
     * Actor que se encarga de recibir mensajes de algún otro actor y enviarlos al cliente remoto.
     * Enviará los mensajes por la red.
     */
    private val gateway: Actor = newGateway(networkConnection.messageOutput).start

    /**
     * Actor externo que será el destinatario de los mensajes recibidos del cliente remoto.
     */
    private val clientActor = newClient.start

    /**
     * Actor interno que se encarga de leer los mensajes provenientes de la red (que vienen del cliente remoto) y
     * los despacha al destinatario (clientActor)
     */
    private val networkListener = newNetworkListener(networkConnection.messageInput).start

    // send actors to client actor
    clientActor ! NetworkActors(gateway, networkListener)

    // send client actor to network and gateway actors
    networkListener ! SetPeerActor(clientActor)
    gateway ! SetPeerActor(clientActor)

    // factory methods
    protected def newGateway(output: MessageOutput): Actor

    protected def newNetworkListener(input: MessageInput): Actor

    protected def newClient(): Actor

    override def disconnect = {
        if (clientActor != null)
            clientActor ! TerminateActor()
        if (gateway != null)
            gateway ! TerminateActor()
        if (networkListener != null)
            networkListener ! TerminateActor()

        networkConnection.close
        disconnectCallback.disconnect(this)
    }
}
