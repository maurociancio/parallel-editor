package ar.noxit.paralleleditor.server

import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import org.springframework.beans.factory.InitializingBean
import ar.noxit.paralleleditor.kernel.remote.{KernelService, SocketKernelService}
import reflect.BeanProperty

class OneDefaultDocKernelService(private val port: Int) extends SocketKernelService(port) {
    override protected def newKernel = {
        val kernel = new BasicKernel
        val session = kernel.login("test")
        kernel.newDocument(session, "new_document")
        session.logout
        kernel
    }
}

class RunKernelServer extends InitializingBean {
    @BeanProperty
    var service: KernelService = _

    def afterPropertiesSet = {
        System.setProperty("actors.corePoolSize", "50")
        service.startService
    }
}
