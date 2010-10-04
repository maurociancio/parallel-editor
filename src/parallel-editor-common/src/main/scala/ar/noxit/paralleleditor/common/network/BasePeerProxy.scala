package ar.noxit.paralleleditor.common.remote

import actors.Actor
import ar.noxit.paralleleditor.common.network.{DisconnectClientCallback, NetworkConnection, MessageOutput, MessageInput}

trait Client {
    def disconnect
}

trait ClientActorFactory {
    def newClientActor(client: Client): Actor
}

case class NetworkActors(val gateway: Actor, val listener: Actor)
case class SetClientActor(val client: Actor)
case class TerminateActor

/**
 * Esta clase encapsula la referencia a un cliente remoto proveyendo una interfaz para definir el destinatario
 * de los mensajes que recibirán por la red y permite obtener un actor al cual enviarle mensajes para que lleguen
 * al cliente.
 */
abstract class BasePeerProxy(private val networkConnection: NetworkConnection,
                             private val disconnectCallback: DisconnectClientCallback) extends Client {

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
    networkListener ! SetClientActor(clientActor)
    gateway ! SetClientActor(clientActor)

    // factory methods
    protected def newGateway(output: MessageOutput): Actor

    protected def newNetworkListener(input: MessageInput): Actor

    protected def newClient(): Actor

    override def disconnect = {
        clientActor ! TerminateActor()
        networkConnection.close
        disconnectCallback.disconnect(this)
    }
}
