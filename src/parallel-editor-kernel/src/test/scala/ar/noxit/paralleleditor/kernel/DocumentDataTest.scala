package ar.noxit.paralleleditor.kernel

import basic.{BasicSession, BasicKernel}
import messages.SubscriptionResponse
import operations.GetTextOperation
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import Assert._
import ar.noxit.paralleleditor.common.operation.{DeleteTextOperation, AddTextOperation}

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

        session.installOnUpdateCallback(new UpdateCallback {
            override def update(message: AnyRef) = {
                message match {
                    case SubscriptionResponse(ds, content) => docSession = ds
                    case _ => {}
                }
            }
        })

        kernel.newDocument(session, "title")
        Thread.sleep(300)
    }

    // FIXME
//    @Test
//    def testDocumentData: Unit = {
//        docSession.applyChange(new AddTextOperation("hello", 0))
//
//        val text = new GetTextOperation
//        docSession.applyChange(text)
//
//        Thread.sleep(300)
//        assertEquals(text.text, "hello")
//    }
//
//    @Test
//    def testAdd2TextToDocument: Unit = {
//        docSession.applyChange(new AddTextOperation("hello", 0))
//        docSession.applyChange(new AddTextOperation("-bye-", 1))
//
//        val text = new GetTextOperation
//        docSession.applyChange(text)
//
//        Thread.sleep(300)
//        assertEquals(text.text, "h-bye-ello")
//    }
//
//    @Test
//    def testAdd2TextToDocumentAndDelete: Unit = {
//        docSession.applyChange(new AddTextOperation("hello", 0))
//        docSession.applyChange(new AddTextOperation("-bye-", 1))
//        docSession.applyChange(new DeleteTextOperation(startPos = 1, size = 2))
//
//        val text = new GetTextOperation
//        docSession.applyChange(text)
//
//        Thread.sleep(300)
//        assertEquals(text.text, "hye-ello")
//    }
}
