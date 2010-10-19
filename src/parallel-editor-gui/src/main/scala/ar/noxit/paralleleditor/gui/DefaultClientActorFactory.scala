package ar.noxit.paralleleditor.gui

import java.net.Socket
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection
import org.springframework.beans.factory.{BeanFactory, BeanFactoryAware}
import remotes.{LocalClientActorFactory, RemoteServerProxy}

class DefaultClientActorFactory extends ClientActorFactory with BeanFactoryAware {
    var beanFactory: BeanFactory = _

    def newActor(host: String, port: Int, docs: Documents) = {
        val socket = new Socket(host, port)

        val factory = beanFactory.getBean("internalClientActorFactory", docs).asInstanceOf[LocalClientActorFactory]
        // TODO resolver el tema de la conexión, que se cierra on disconnect
        new RemoteServerProxy(new SocketNetworkConnection(socket), factory)

        factory.clientActor
    }

    def setBeanFactory(beanFactory: BeanFactory) = {
        this.beanFactory = beanFactory
    }
}
