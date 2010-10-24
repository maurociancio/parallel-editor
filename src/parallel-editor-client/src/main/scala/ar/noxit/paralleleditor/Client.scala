package ar.noxit.paralleleditor.client


import actors.Actor

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

object SynchronizationSessionFactory extends LocalClientActorFactory{

    def getSyncServerSession(host:String, port: Int) {

        val socket = new Socket(host, port)
     //   val factory = beanFactory.getBean("internalClientActorFactory", docs).asInstanceOf[LocalClientActorFactory]
        // TODO resolver el tema de la conexión, que se cierra on disconnect
//        new RemoteServerProxy(new SocketNetworkConnection(socket), factory)

        factory.clientActor
    }

}