package ar.noxit.paralleleditor.kernel.service

import ar.noxit.paralleleditor.kernel.remote.SocketKernelService
import ar.noxit.paralleleditor.kernel.basic.BasicKernel

class BaseKernelService(private val port: Int) extends SocketKernelService(port) {
    override protected def newKernel = {
        val kernel = new BasicKernel
        val session = kernel.login("test")
        kernel.newDocument(session, "new_document")
        session.logout
        kernel
    }
}

object RunKernelServer {
    def main(args: Array[String]) {
        System.setProperty("actors.corePoolSize", "50")
        new BaseKernelService(5000).start
    }
}
