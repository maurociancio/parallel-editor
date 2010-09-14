package ar.noxit.paralleleditor.kernel

import org.junit._
import Assert._

@Test
class KernelTest {

    @Test
    def testOK() = {
        val factory = new BasicKernelFactory
        val kernel = factory buildKernel

        assertEquals(kernel.documentCount, 0)

        val session = kernel login "username"
        val handler = kernel newDocument(session, "title")

        assertEquals(kernel.documentCount, 1)
    }
}
