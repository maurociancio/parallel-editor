package ar.noxit.paralleleditor.kernel

import org.scalatest.junit.AssertionsForJUnit
import org.junit._
import ar.noxit.paralleleditor.kernel.remote.{ClientService, KernelService}

@Test
class ConnectionTest extends AssertionsForJUnit {

    @Test
    def testConnection = {
        val kernelService = new KernelService
        val clientService = new ClientService

        kernelService.start
        clientService.start

        Thread.sleep(1000)
    }
}