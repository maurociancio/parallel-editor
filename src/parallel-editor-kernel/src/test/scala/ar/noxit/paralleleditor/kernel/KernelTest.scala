package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.exceptions.DocumentTitleAlreadyExitsException
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

        val docSession = kernel newDocument(session, "title")
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel.documentSuscriberCount("title").get, 1)

        docSession.unsuscribe
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel sessionCount, 1)
        assertEquals(kernel.documentSuscriberCount("title").get, 0)

        session.logout
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel sessionCount, 0)
        assertEquals(kernel.documentSuscriberCount("title").get, 0)
    }

    @Test
    def testLogout = {
        val session = kernel login "username"
        val docSession = kernel newDocument(session, "title")

        assertEquals(kernel sessionCount, 1)
        assertEquals(kernel.documentSuscriberCount("title").get, 1)

        session.logout

        assertEquals(kernel sessionCount, 0)
        assertEquals(kernel.documentSuscriberCount("title").get, 0)
    }

    @Test
    def testTwoDocumentsWithSameTitle : Unit = {
        val session = kernel login "username"
        val docSession = kernel newDocument(session, "title")

        // espera que la llamada a newDocument lance excepción
        intercept[DocumentTitleAlreadyExitsException] {
            kernel newDocument(session, "title")
        }
    }

    @Test
    def testNonexistentDocumentSuscriberCount : Unit = {
        intercept[NoSuchElementException] {
            (kernel documentByTitle "pirulo").get
        }
    }
}
