package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.network.{MessageOutput, MessageInput, NetworkConnection}
import actors.{TIMEOUT, Actor}

trait Client {
    def disconnect
}

trait ClientActorFactory {
    def newClientActor(client: Client): Actor
}

case class NetworkActors(val gateway: Actor, val listener: Actor)
case class ClientActor(val client: Actor)

/**
 * Esta clase encapsula la referencia a un cliente remoto proveyendo una interfaz para definir el destinatario
 * de los mensajes que recibirán por la red y permite obtener un actor al cual enviarle mensajes para que lleguen
 * al cliente.
 */
class RemoteClientProxy(private val networkConnection: NetworkConnection,
                        private val clientActorFactory: ClientActorFactory,
                        private val disconnectCallback: DisconnectClientCallback) extends Client {
    /**
     * Actor que se encarga de recibir mensajes de algún otro actor y enviarlos al cliente remoto.
     * Enviará los mensajes por la red.
     */
    private val gateway: Actor = new GatewayActor(networkConnection.messageOutput).start

    /**
     * Actor externo que será el destinatario de los mensajes recibidos del cliente remoto.
     */
    private val clientActor = clientActorFactory.newClientActor(this).start

    /**
     * Actor interno que se encarga de leer los mensajes provenientes de la red (que vienen del cliente remoto) y
     * los despacha al destinatario (clientActor)
     */
    private val networkListener = new NetworkListenerActor(networkConnection.messageInput).start

    // send actors to client actor
    clientActor ! NetworkActors(gateway, networkListener)

    // send client actor to network and gateway actors
    networkListener ! ClientActor(clientActor)
    gateway ! ClientActor(clientActor)

    override def disconnect = {
        clientActor ! "EXIT";
        disconnectCallback.disconnect(this)
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class NetworkListenerActor(private val input: MessageInput) extends Actor with Loggable {
    private var client: Actor = _
    private val timeout = 5000

    override def act = {
        client = receiveClient

        try {
            processMessages
        } catch {
            case e: Exception => {
                warn(e, "Exception thrown during receive")
                doExit
            }
        }
    }

    private def processMessages: Unit = {
        while (true) {
            val inputMessage: Any = input.readMessage
            trace("Message received %s", inputMessage)

            client ! inputMessage
        }
    }

    private def receiveClient = {
        receiveWithin(timeout) {
            case ClientActor(client) => {
                trace("client actor received")
                client
            }
            case "EXIT" => doExit
            case TIMEOUT => doExit
        }
    }

    private def doExit = {
        // TODO cambiar por un mensaje
        if (client != null)
            client ! "EXIT"
        exit
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class GatewayActor(private val output: MessageOutput) extends Actor with Loggable {
    private var client: Actor = _
    private val timeout = 5000

    override def act = {
        client = receiveClient

        loop {
            react {
                case "EXIT" => {
                    trace("Exit received, exiting")
                    doExit
                }
                case message: Any => {
                    trace("writing message to client [%s]", message)
                    try {
                        output writeMessage message
                    }
                    catch {
                        case e: Exception => {
                            warn(e, "Exception thrown during send")
                            doExit
                        }
                    }
                }
            }
        }
    }

    private def receiveClient = {
        receiveWithin(timeout) {
            case ClientActor(client) => {
                trace("client actor received")
                client
            }
            case "EXIT" => doExit
            case TIMEOUT => doExit
        }
    }

    private def doExit = {
        // TODO cambiar por un mensaje
        if (client != null)
            client ! "EXIT"
        exit
    }
}
