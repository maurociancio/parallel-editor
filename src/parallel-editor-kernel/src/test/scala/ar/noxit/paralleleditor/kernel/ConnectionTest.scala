package ar.noxit.paralleleditor.kernel

import org.scalatest.junit.AssertionsForJUnit
import org.junit._
import ar.noxit.paralleleditor.kernel.remote.{ KernelServer}
import java.net.Socket

@Test
class ConnectionTest extends AssertionsForJUnit {

    @Test
    def testConnection = {
    /*    val kernelService = new KernelServer
        val clientService = new RemoteServerProxy(new Socket("localhost", 5000))

        kernelService.start
        clientService

        Thread.sleep(1000)*/
    }
}
