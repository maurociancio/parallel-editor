package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.actors.ClientActor
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.KernelActor
import ar.noxit.paralleleditor.kernel.remote.KernelService
import org.junit._
import Assert._
import org.scalatest.junit.AssertionsForJUnit

@Test
class KernelConnectionTest extends AssertionsForJUnit {

    @Test
    def testDocumentCount : Unit = {
        val kernel = new BasicKernel
        val ka = new KernelActor(kernel)
        ka.start

        Thread.sleep(1000)
        new ClientActor(ka, "myname").start
    }
}
