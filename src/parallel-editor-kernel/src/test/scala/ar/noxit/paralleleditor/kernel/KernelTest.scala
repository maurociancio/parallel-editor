package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import org.junit._
import Assert._
import org.scalatest.junit.AssertionsForJUnit

@Test
class KernelTest extends AssertionsForJUnit {

    var factory: BasicKernelFactory = _
    var kernel: BasicKernel = _

    @Before
    def setUp = {
        factory = new BasicKernelFactory
        kernel = factory buildKernel
    }

    @Test
    def testDocumentCount : Unit = {
        assertEquals(kernel documentCount, 0)
        assertEquals(kernel sessionCount, 0)

        val session = kernel login "username"
        assertEquals(kernel sessionCount, 1)

        val handler = kernel newDocument(session, "title")
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel.documentByTitleCount("title").get, 1)

        handler.unsuscribe
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel sessionCount, 1)
        assertEquals(kernel.documentByTitleCount("title").get, 0)
    }

//        intercept[StringIndexOutOfBoundsException] {
//            "concise".charAt(-1)
//        }
}
