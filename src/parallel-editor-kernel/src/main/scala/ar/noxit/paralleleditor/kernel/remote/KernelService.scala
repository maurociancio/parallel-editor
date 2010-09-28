package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.{KernelActor, ClientActor}
import actors.{DaemonActor, Actor}
import java.net.ServerSocket
import ar.noxit.paralleleditor.kernel.network.{NetworkConnection, SocketNetworkConnection}
import ar.noxit.paralleleditor.kernel.Kernel

trait Finalizable {
    def finalizeNow
}

/**
 * Actor que se encarga de escuchar conexiones entrantes y crear una representacion del cliente remoto
 * a partir de la conexión recibida.
 */
abstract class KernelService extends DaemonActor {
    private var sExit = false

    protected var kernel: Kernel = _
    protected var ka: Actor = _
    protected var clientActorFactory: ClientActorFactory = _
    private var clients = List[Finalizable]()

    def initialize = {
        kernel = newKernel
        ka = newKernelActor
        clientActorFactory = newClientActorFactory
        this
    }

    def act = {
        while (!sExit) {
            // new network connection
            val networkConnection = newNetworkConnection()

            // new client
            val client = newClient(networkConnection)
            clients = client :: clients
        }
    }

    def stop {
        sExit = true
        clients.foreach{ _.finalizeNow }
    }

    protected def newClient(networkConnection: NetworkConnection): Finalizable = {
        new RemoteClientProxy(networkConnection, clientActorFactory)
    }

    protected def newNetworkConnection(): NetworkConnection

    protected def newKernel: Kernel = {
        new BasicKernel
    }

    protected def newKernelActor: Actor = {
        new KernelActor(kernel).start
    }

    protected def newClientActorFactory: ClientActorFactory = {
        new BasicClientActorFactory(ka)
    }
}

class BasicClientActorFactory(private val kernel: Actor) extends ClientActorFactory {
    override def newClientActor(finalizable: Finalizable) = {
        new ClientActor(kernel, finalizable)
    }
}

class SocketKernelService(private val port: Int) extends KernelService {
    private val server = new ServerSocket(port)

    protected def newNetworkConnection(): NetworkConnection = {
        val clientSocket = server accept;
        new SocketNetworkConnection(clientSocket)
    }

    override def stop {
        server.close
        super.stop
    }
}
