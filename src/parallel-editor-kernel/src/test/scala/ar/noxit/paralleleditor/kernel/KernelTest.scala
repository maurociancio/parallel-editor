package ar.noxit.paralleleditor.kernel

import basic.sync.SynchronizerAdapterFactory
import ar.noxit.paralleleditor.common.BasicXFormStrategy
import basic.{BasicSession, BasicKernel}
import exceptions.{DocumentDeleteUnexistantException, DocumentTitleAlreadyExitsException}
import messages.{DocumentDeleted, SubscriptionResponse}
import org.junit._
import Assert._
import org.scalatest.junit.AssertionsForJUnit
import scala.actors.Future

@Test
class KernelTest extends AssertionsForJUnit {
    var kernel: BasicKernel = _
    var docSession: DocumentSession = _

    @Before
    def setUp = {
        kernel = new BasicKernel;
        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        kernel.sync = synchronizerAdapterFactory
        kernel.timeout = 5000
        docSession = null
    }

    @Test
    def testDocumentCount: Unit = {
        assertEquals(kernel documentCount, 0)
        assertEquals(kernel sessionCount, 0)

        val session = kernel login "username"
        assertEquals(kernel sessionCount, 1)

        installMockCallback(session)

        kernel newDocument (session, "title")
        Thread.sleep(300)
        assertNotNull(docSession)

        assertEquals(kernel documentCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 1)

        docSession.unsubscribe
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel sessionCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 0)

        session.logout
        assertEquals(kernel documentCount, 1)
        assertEquals(kernel sessionCount, 0)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 0)

        Thread.sleep(300)
    }

    @Test
    def testLogout = {
        val session = kernel login "username"
        kernel newDocument (session, "title")

        installMockCallback(session)

        assertEquals(kernel sessionCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 1)

        session.logout

        assertEquals(kernel sessionCount, 0)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 0)
    }

    @Test
    def testTwoDocumentsWithSameTitle: Unit = {
        val session = kernel login "username"
        kernel newDocument (session, "title")

        // espera que la llamada a newDocument lance excepción
        intercept[DocumentTitleAlreadyExitsException] {
            kernel newDocument (session, "title")
        }
    }

    @Test
    def testNonexistentDocumentSuscriberCount: Unit = {
        intercept[NoSuchElementException] {
            (kernel documentByTitle "pirulo").get
        }
    }

    @Test
    def testNonexistentDocumentDeletion: Unit = {
        val session = kernel login "username"
        intercept[DocumentDeleteUnexistantException] {
            kernel.deleteDocument(session, "pirulo")
        }
    }

    @Test
    def testDocumentInUseException: Unit = {

        //logueo a dos usuarios
        val session = kernel login "username1"
        assertEquals(kernel sessionCount, 1)

        val session2 = kernel login "username2"
        assertEquals(kernel sessionCount, 2)

        installMockCallback(session)
        installMockCallback(session2)

        //creo un documento que y suscribo a los dos usuarios
        kernel newDocument (session, "title")
        Thread.sleep(300)
        assertNotNull(docSession)
        assertEquals(kernel documentCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 1)

        val docSession2 = kernel subscribe (session2, "title")
        Thread.sleep(300)
        assertNotNull(docSession2)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 2)

        //intento eliminar el doc que esta siendo usado por 2 usuarios

        kernel.deleteDocument(session, "title")
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 2)

        //deslogueo usuario 2
        session2 logout


        var docDeleted = false
        session.installOnUpdateCallback(new UpdateCallback {
            override def update(message: AnyRef) = {
                message match {
                    case DocumentDeleted(title) => docDeleted = true
                    case _ => {}
                }
            }
        })

        //intento eliminar el documento nuevamente
        kernel.deleteDocument(session, "title")
        Thread.sleep(500)

        if (docDeleted)
            kernel.removeDeletedDocument("title")

        assertEquals(kernel.documentCount, 0)
    }

    def calculateSubscriberCount(count: Option[Future[Int]]) = {
        Thread.sleep(300)
        count.get.apply
    }

    def installMockCallback(session: BasicSession) = {
        session.installOnUpdateCallback(new UpdateCallback {
            override def update(message: AnyRef) = {
                message match {
                    case SubscriptionResponse(ds, content) => docSession = ds
                    case _ => {}
                }
            }
        })
    }
}
