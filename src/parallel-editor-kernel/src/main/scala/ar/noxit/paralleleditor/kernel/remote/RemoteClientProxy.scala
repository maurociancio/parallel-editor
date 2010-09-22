package ar.noxit.paralleleditor.kernel.remote

import java.net.Socket
import scala.actors.Actor
import scala.actors.Actor._
import java.io._

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
    private val recipientActor = clientActorFactory.newClientActor(gateway)

    /**
     * Actor interno que se encarga de leer los mensajes provenientes de la red (que vienen del cliente remoto) y
     * los despacha al destinatario (recipient)
     */
    private val networkListener = new NetworkListenerActor(recipientActor, new ObjectInputStream(socket.getInputStream)).start
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class NetworkListenerActor(recipient: Actor, input: ObjectInput) extends Actor {
    override def act = {
        // TODO definir mensaje de salida
        var exit = false
        while (!exit) {
            val inputMessage: Any = input.readObject()
            println("received " + inputMessage)
            recipient ! inputMessage
        }
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class GatewayActor(output: ObjectOutput) extends Actor {
    override def act = {
        // TODO definir mensaje de salida
        var exit = false

        loopWhile(!exit) {
            react {
                case message: Any => output writeObject message
            }
        }
    }
}