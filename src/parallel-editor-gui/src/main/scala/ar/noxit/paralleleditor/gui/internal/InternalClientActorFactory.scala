package ar.noxit.paralleleditor.gui.internal

import ar.noxit.paralleleditor.gui.remotes.LocalClientActorFactory
import org.springframework.beans.factory.{BeanFactory, BeanFactoryAware}
import ar.noxit.paralleleditor.gui.Documents
import actors.Actor

class InternalClientActorFactory(private val docs: Documents) extends LocalClientActorFactory with BeanFactoryAware {
    var clientActor: Actor = _

    override def newLocalClientActor = {
        clientActor
    }

    def setBeanFactory(beanFactory: BeanFactory) = {
        if (clientActor == null) {
            val obj: AnyRef = beanFactory.getBean("clientActor", docs)
            clientActor = obj.asInstanceOf[Actor]
        }
    }
}
