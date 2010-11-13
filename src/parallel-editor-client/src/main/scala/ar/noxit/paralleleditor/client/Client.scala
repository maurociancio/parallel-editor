package ar.noxit.paralleleditor.client

import actors.Actor
import converter.DefaultResponseConverter
import remote.RemoteServerProxy
import java.net.Socket
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection
import ar.noxit.paralleleditor.common.converter.{DefaultRemoteOperationConverter, DefaultMessageConverter}

trait Session {
    def !(msg: Any)
}

object Session {
    implicit def sessionActor2Session(actor: Actor): Session =
        new Session {
            def !(msg: Any) = actor ! msg
        }
}

trait JSession {
    def send(msg: Any)
}

object JSession {
    implicit def session2JSession(session: Session): JSession =
        new JSession {
            def send(msg: Any) = session ! msg
        }
}

trait Documents {
    /**
     * See ClientMessages.scala
     */
    def process(msg: CommandFromKernel)
}

trait LocalClientActorFactory {
    def newLocalClientActor: Actor
}

class InternalClientActorFactory(private val docs: Documents) extends LocalClientActorFactory {
    val clientActor = new ClientActor(docs)
    clientActor.responseConverter = new DefaultResponseConverter
    clientActor.converter = new DefaultMessageConverter(new DefaultRemoteOperationConverter)

    override def newLocalClientActor = clientActor
}

object SessionFactory {
    def newSession(host: String, port: Int, adapter: Documents): Session = {
        val socket = new Socket(host, port)
        val factory = new InternalClientActorFactory(adapter)

        // TODO resolver el tema de la conexión, que se cierra on disconnect
        new RemoteServerProxy(new SocketNetworkConnection(socket), factory)
        factory.clientActor
    }

    def newJSession(host: String, port: Int, adapter: Documents): JSession =
        newSession(host, port, adapter)
}
