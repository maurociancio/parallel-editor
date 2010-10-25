package ar.noxit.paralleleditor.client

import actors.Actor
import java.net.Socket
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection

trait Documents {
    def byName(title: String): Option[ConcurrentDocument]

    def changeDocList(l: List[String])

    def createDocument(title: String, content: String)

    def usernameTaken
}

trait LocalClientActorFactory {
    def newLocalClientActor: Actor
}

class InternalClientActorFactory(private val docs: Documents) extends LocalClientActorFactory {
    override def newLocalClientActor = {
        new ClientActor(docs)
    }
}

object SynchronizationSessionFactory {
    def getSyncServerSession(host: String, port: Int, docs: Documents): Actor = {
        val socket = new Socket(host, port)
        val factory = new InternalClientActorFactory(docs)
        // TODO resolver el tema de la conexión, que se cierra on disconnect
        new RemoteServerProxy(new SocketNetworkConnection(socket), factory)

        factory.newLocalClientActor
    }
}
