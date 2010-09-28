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
    val gateway: Actor = new GatewayActor(new ObjectOutputStream(socket.getOutputStream)).start

    /**
     * Actor externo que será el destinatario de los mensajes recibidos del cliente remoto.
     */
    private val recipientActor = clientActorFactory.newClientActor(gateway).start

    /**
     * Actor interno que se encarga de leer los mensajes provenientes de la red (que vienen del cliente remoto) y
     * los despacha al destinatario (recipient)
     */
    private val networkListener = new NetworkListenerActor(recipientActor, new ObjectInputStream(socket.getInputStream)).start
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class NetworkListenerActor(private val recipient: Actor, private val input: ObjectInput) extends Actor with Loggable {
    override def act = {
        try {
            while(true) {
                val inputMessage: Any = input.readObject()
                trace("Message received %s", inputMessage)

                recipient ! inputMessage
            }
        } catch {
            case e: Exception => {
                warn(e, "Exception thrown during receive")

                // TODO cambiar por un mensaje
                recipient ! "EXIT"
            }
        }
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class GatewayActor(private val output: ObjectOutput) extends Actor with Loggable {
    override def act = {
        var exit = false

        loopWhile(!exit) {
            react {
                case "EXIT" => { // TODO cambiar mensaje
                    trace("Exit received, exiting")
                    exit = true
                }
                case message: Any => {
                    trace("writing message to client [%s]", message)
                    output writeObject message
                }
            }
        }
    }
}
