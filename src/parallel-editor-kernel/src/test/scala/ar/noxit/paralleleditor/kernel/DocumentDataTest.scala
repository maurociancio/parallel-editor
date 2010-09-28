package ar.noxit.paralleleditor.kernel

import basic.{BasicSession, BasicKernel}
import operations.{DeleteTextOperation, GetTextOperation, AddTextOperation}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import Assert._

@Test
class DocumentDataTest extends AssertionsForJUnit {

    var factory: BasicKernelFactory = _
    var kernel: BasicKernel = _
    var session: BasicSession = _
    var docSession: DocumentSession = _

    @Before
    def setUp = {
        factory = new BasicKernelFactory
        kernel = factory buildKernel;
        session = kernel.login("username")
        // FIXME
//        docSession = kernel.newDocument(session, "title")
    }

    @Test
    def testDocumentData : Unit = {
        docSession.applyChange(new AddTextOperation("hello", 0))

        val text = new GetTextOperation
        docSession.applyChange(text)

        Thread.sleep(300)
        assertEquals(text.text, "hello")
    }

    @Test
    def testAdd2TextToDocument : Unit = {
        docSession.applyChange(new AddTextOperation("hello", 0))
        docSession.applyChange(new AddTextOperation("-bye-", 1))

        val text = new GetTextOperation
        docSession.applyChange(text)

        Thread.sleep(300)
        assertEquals(text.text, "h-bye-ello")
    }

    @Test
    def testAdd2TextToDocumentAndDelete : Unit = {
        docSession.applyChange(new AddTextOperation("hello", 0))
        docSession.applyChange(new AddTextOperation("-bye-", 1))
        docSession.applyChange(new DeleteTextOperation(startPos = 1, size = 2))

        val text = new GetTextOperation
        docSession.applyChange(text)

        Thread.sleep(300)
        assertEquals(text.text, "hye-ello")
    }
}
