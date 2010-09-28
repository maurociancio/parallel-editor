package ar.noxit.paralleleditor.kernel.remote

import java.net.Socket
import scala.actors.Actor
import java.io._
import ar.noxit.paralleleditor.common.logger.Loggable

/**
 * Esta clase encapsula la referencia a un cliente remoto proveyendo una interfaz para definir el destinatario
 * de los mensajes que recibirán por la red y permite obtener un actor al cual enviarle mensajes para que lleguen
 * al cliente.
 */
class RemoteClientProxy(socket: Socket, clientActorFactory: ClientActorFactory) {
    /**
     * Actor que se encarga de recibir mensajes de algún otro actor y enviarlos al cliente remoto.
     * Enviará los mensajes por la red.
     */
    private val gateway: Actor = new GatewayActor(new ObjectOutputStream(socket.getOutputStream)).start

    /**
     * Actor externo que será el destinatario de los mensajes recibidos del cliente remoto.
     */
    private val clientActor = clientActorFactory.newClientActor.start

    /**
     * Actor interno que se encarga de leer los mensajes provenientes de la red (que vienen del cliente remoto) y
     * los despacha al destinatario (clientActor)
     */
    private val networkListener = new NetworkListenerActor(new ObjectInputStream(socket.getInputStream)).start

    // send actors to client actor
    clientActor ! ("input", networkListener)
    clientActor ! ("gateway", gateway)

    // send client actor to network and gateway actors
    networkListener ! ("client", clientActor)
    gateway ! ("client", clientActor)
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class NetworkListenerActor(private val input: ObjectInput) extends Actor with Loggable {
    private var clientActor: Actor = _
    private val timeout = 5000

    override def act = {
        receiveWithin(timeout) {
            case ("client", client: Actor) if clientActor == null => {
                trace("client actor received")
                clientActor = client
            }
            case "EXIT" => exit
            // TODO timeout
        }

        try {
            processMessages
        } catch {
            case e: Exception => {
                warn(e, "Exception thrown during receive")

                // TODO cambiar por un mensaje
                clientActor ! "EXIT"
            }
        }
    }

    private def processMessages: Unit = {
        while (true) {
            val inputMessage: Any = input.readObject()
            trace("Message received %s", inputMessage)

            clientActor ! inputMessage
        }
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class GatewayActor(private val output: ObjectOutput) extends Actor with Loggable {
    private var clientActor: Actor = _
    private val timeout = 5000

    override def act = {
        receiveWithin(timeout) {
            case ("client", client: Actor) if clientActor == null => {
                trace("client actor received")
                clientActor = client
            }
            case "EXIT" => this.exit()
            // TODO timeout
        }

        var exit = false
        loopWhile(!exit) {
            react {
                case "EXIT" => { // TODO cambiar mensaje
                    trace("Exit received, exiting")
                    exit = true
                }
                case message: Any => {
                    trace("writing message to client [%s]", message)
                    try {
                        output writeObject message
                    }
                    catch {
                        case e: Exception => {
                            warn(e, "Exception thrown during send")
                            clientActor ! "EXIT" // TODO
                            exit = true
                        }
                    }
                }
            }
        }
    }
}
