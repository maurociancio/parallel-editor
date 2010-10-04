package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import actors.{DaemonActor, Actor}
import java.net.ServerSocket
import ar.noxit.paralleleditor.common.network.{NetworkConnection, SocketNetworkConnection}
import ar.noxit.paralleleditor.kernel.Kernel
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.actors.{ClientActor, KernelActor}


/**
 * Actor que se encarga de escuchar conexiones entrantes y crear una representacion del cliente remoto
 * a partir de la conexión recibida.
 */
abstract class KernelService extends DaemonActor with Loggable {
    private var sExit = false

    protected var kernel: Kernel = _
    protected var ka: Actor = _
    protected var clientActorFactory: ClientActorFactory = _
    protected val clientList = new ClientListActor
    clientList.start

    protected def initialize = {
        kernel = newKernel
        ka = newKernelActor
        clientActorFactory = newClientActorFactory
        this
    }

    def act = {
        trace("Kernel Service starting")
        initialize;

        while (!sExit) {
            trace("Waiting for clients")

            // new network connection
            val networkConnection = newNetworkConnection()

            // new client
            try {
                val client = newClient(networkConnection)
                clientList ! AddClient(client)
            }
            catch {
                case e: Exception =>
                    warn(e, "exception during newClient")
            }
        }
        trace("Kernel Service dying")
    }

    def stop {
        sExit = true
        clientList ! RemoveAllClients()
    }

    protected def newClient(networkConnection: NetworkConnection): Client =
        new RemoteClientProxy(networkConnection, clientActorFactory, clientList)

    protected def newNetworkConnection(): NetworkConnection

    protected def newKernel: Kernel = new BasicKernel

    protected def newKernelActor: Actor = new KernelActor(kernel).start

    protected def newClientActorFactory: ClientActorFactory = new BasicClientActorFactory(ka)
}

class BasicClientActorFactory(private val kernel: Actor) extends ClientActorFactory {
    override def newClientActor(client: Client) = new ClientActor(kernel, client)
}

class SocketKernelService(private val port: Int) extends KernelService {
    private val server = new ServerSocket(port)

    override protected def newNetworkConnection(): NetworkConnection = {
        val clientSocket = server accept;
        new SocketNetworkConnection(clientSocket)
    }

    override def stop {
        super.stop
        server.close
    }
}
