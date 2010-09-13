package ar.noxit.paralleleditor.kernel

import org.junit._
import Assert._

@Test
class KernelTest {

    @Test
    def testOK() = {
        val factory = new BasicKernelFactory
        val kernel = factory buildKernel

        val session = kernel login "username"
        val handler = kernel newDocument(session, "title")
    }
}
