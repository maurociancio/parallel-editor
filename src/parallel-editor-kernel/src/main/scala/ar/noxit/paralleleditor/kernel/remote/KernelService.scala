package ar.noxit.paralleleditor.kernel.remote

import java.net.ServerSocket
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.{KernelActor, ClientActor}
import actors.{DaemonActor, Actor}

trait ClientActorFactory {
    def newClientActor(gateway: Actor): Actor
}

class BasicClientActorFactory(kernel: Actor) extends ClientActorFactory {
    override def newClientActor(gateway: Actor) = {
        new ClientActor(kernel, gateway)
    }
}

/**
 * Actor que se encarga de escuchar conexiones entrantes y crear una representacion del cliente remoto
 * a partir de la conexión recibida.
 */
class KernelServer extends DaemonActor {
    var sExit = false
    val server = new ServerSocket(5000)
    val kernel = new BasicKernel
    val ka = new KernelActor(kernel).start

    def act = {
        val clientActorFactory = new BasicClientActorFactory(ka)

        while (!sExit) {
            // client socket
            val clientSocket = server accept

            // new client
            val client = new RemoteClientProxy(clientSocket, clientActorFactory)
        }
    }
}
