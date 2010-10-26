package ar.noxit.paralleleditor.kernel.remote

import actors.{DaemonActor, Actor}
import java.net.ServerSocket
import ar.noxit.paralleleditor.common.network.{NetworkConnection, SocketNetworkConnection}
import ar.noxit.paralleleditor.kernel.Kernel
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.remote.{Peer, PeerActorFactory}
import reflect.BeanProperty
import ar.noxit.paralleleditor.common.converter._
import ar.noxit.paralleleditor.kernel.actors.{RemoteMessageConverter, ToKernelConverter, ClientActor, KernelActor}
import ar.noxit.paralleleditor.kernel.actors.converter.{DefaultToKernelConverter, DefaultRemoteMessageConverter}

trait KernelService {
    def startService
}

/**
 * Actor que se encarga de escuchar conexiones entrantes y crear una representacion del cliente remoto
 * a partir de la conexión recibida.
 */
abstract class BaseKernelService extends DaemonActor with Loggable with KernelService {
    private var sExit = false

    @BeanProperty
    protected var kernel: Kernel = _

    protected var ka: Actor = _
    protected var clientActorFactory: PeerActorFactory = _
    protected val clientList = new ClientListActor
    clientList.start

    def startService = this.start

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

    protected def newClient(networkConnection: NetworkConnection): Peer =
        new RemoteClientProxy(networkConnection, clientActorFactory, clientList)

    protected def newNetworkConnection(): NetworkConnection

    protected def newKernel: Kernel = kernel

    protected def newKernelActor: Actor = new KernelActor(kernel).start

    protected def newClientActorFactory: PeerActorFactory =
        new BasicClientActorFactory(ka)
}

class BasicClientActorFactory(private val kernel: Actor) extends PeerActorFactory {
    // NO hay problema en inyectarlo por que son todas clases sin estado

    @BeanProperty
    var converter: RemoteDocumentOperationConverter = _
    @BeanProperty
    var messageConverter: MessageConverter = _
    @BeanProperty
    var remoteConverter: RemoteMessageConverter = _
    @BeanProperty
    var toKernelConverter: ToKernelConverter = _

    override def newClientActor(client: Peer) = {
        val actor = new ClientActor(kernel, client)
        actor.remoteDocOpconverter = new DefaultRemoteDocumentOperationConverter(new DefaultSyncOperationConverter(new DefaultEditOperationConverter))
        actor.toKernelConverter = new DefaultToKernelConverter
        actor.remoteConverter = new DefaultRemoteMessageConverter
        actor.messageConverter = new DefaultMessageConverter(new DefaultRemoteOperationConverter)
        actor
    }
}

class SocketKernelService(private val port: Int) extends BaseKernelService {
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
