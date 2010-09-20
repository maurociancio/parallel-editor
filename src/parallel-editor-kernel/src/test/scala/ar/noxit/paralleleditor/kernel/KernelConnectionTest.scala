package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.actors.ClientActor
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.KernelActor
import messages._
import org.junit.Assert._
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import scala.actors.Actor._
import scala.actors.Actor

@Test
class KernelConnectionTest extends AssertionsForJUnit {

    var kernel: BasicKernel = _
    var client: Actor = _
    var ka: Actor = _
    var remoteEchoClient: Actor = _

    @Before
    def setUp : Unit = {
        remoteEchoClient = actor {
            loop {
                receive {
                    case any => println("null actor received " + any)
                }
            }
        }
        kernel = new BasicKernel
        ka = new KernelActor(kernel).start
        client = new ClientActor(ka, remoteEchoClient).start
    }

    @Test
    def testSessionCount : Unit = {
        client ! RemoteLogin("myUsername")
        Thread.sleep(300)

        assertEquals(kernel.sessionCount, 1)

        client ! RemoteLogoutRequest
        Thread.sleep(300)

        assertEquals(kernel.sessionCount, 0)
    }

    @Test
    def testDocumentCount : Unit = {
        client ! RemoteLogin("myUsername")
        client ! RemoteNewDocumentRequest("title")
        Thread.sleep(300)

        assertEquals(kernel.documentCount, 1)
        assertEquals(kernel.documentSubscriberCount("title").get, 1)

        client ! RemoteLogoutRequest
        Thread.sleep(300)

        assertEquals(kernel.documentCount, 1)
        assertEquals(kernel.documentSubscriberCount("title").get, 0)
    }

    @Test
    def test2Clients : Unit = {
        val client2 = new ClientActor(ka, self).start

        client ! RemoteLogin("myUsername")
        client ! RemoteNewDocumentRequest("title")
        client2 ! RemoteLogin("myUsername2")
        client2 ! RemoteDocumentList

        Thread.sleep(300)

        val docList = receive {
            case RemoteDocumentListResponse(docList) => docList
        }

        assertEquals(docList, List("title"))
        assertEquals(kernel.sessionCount, 2)
    }
}
