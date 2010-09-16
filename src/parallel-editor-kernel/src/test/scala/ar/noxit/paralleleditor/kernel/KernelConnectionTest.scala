package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.remote.KernelService
import org.junit._
import Assert._
import org.scalatest.junit.AssertionsForJUnit

@Test
class KernelConnectionTest extends AssertionsForJUnit {

    @Test
    def testDocumentCount : Unit = {
        val ks = new KernelService
        ks init
    }
}
