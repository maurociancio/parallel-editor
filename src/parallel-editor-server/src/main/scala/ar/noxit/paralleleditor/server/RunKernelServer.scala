package ar.noxit.paralleleditor.server

import ar.noxit.paralleleditor.kernel.remote.{KernelService, SocketKernelService}
import reflect.BeanProperty
import org.springframework.beans.factory.{BeanFactory, BeanFactoryAware, InitializingBean}
import ar.noxit.paralleleditor.common.remote.PeerActorFactory

class OneDefaultDocKernelService(private val port: Int) extends SocketKernelService(port) with BeanFactoryAware {
    private var bf: BeanFactory = _

    override protected def newKernel = {
        val kernel = super.newKernel
        val session = kernel.login("test")
        kernel.newDocument(session, "new_document")
        session.logout
        kernel
    }

    override protected def newClientActorFactory =
        bf.getBean("clientActorFactory", ka).asInstanceOf[PeerActorFactory]

    def setBeanFactory(beanFactory: BeanFactory) = this.bf = beanFactory
}

class RunKernelServer extends InitializingBean {
    @BeanProperty
    var service: KernelService = _

    def afterPropertiesSet = {
        System.setProperty("actors.corePoolSize", "50")
        service.startService
    }
}
