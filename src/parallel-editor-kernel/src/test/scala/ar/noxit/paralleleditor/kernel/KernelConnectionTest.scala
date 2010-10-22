package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.actors.ClientActor
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.KernelActor
import basic.sync.SynchronizerAdapterFactory
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import scala.actors.Actor._
import ar.noxit.paralleleditor.common.messages._
import Assert._
import ar.noxit.paralleleditor.common.remote.{NetworkActors, Peer}
import scala.actors.{Future, Actor}
import ar.noxit.paralleleditor.common.converter.{DefaultRemoteDocumentOperationConverter, DefaultSyncOperationConverter, DefaultEditOperationConverter}
import ar.noxit.paralleleditor.common.BasicXFormStrategy

object NullPeer extends Peer {
    def disconnect = {}
}

@Test
class KernelConnectionTest extends AssertionsForJUnit {
    var kernel: BasicKernel = _
    var client: ClientActor = _
    var ka: Actor = _
    var remoteEchoClient: Actor = _
    val converter = new DefaultRemoteDocumentOperationConverter(new DefaultSyncOperationConverter(new DefaultEditOperationConverter))

    @Before
    def setUp: Unit = {
        remoteEchoClient = actor {
            loop {
                receive {
                    case any => println("null actor received " + any)
                }
            }
        }
        kernel = new BasicKernel
        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        kernel.sync = synchronizerAdapterFactory
        kernel.timeout = 5000
        ka = new KernelActor(kernel).start
        client = new ClientActor(ka, NullPeer)
        client.converter = converter
        client.start
        client ! NetworkActors(remoteEchoClient, remoteEchoClient)
    }

    @Test
    def testSessionCount: Unit = {
        client ! RemoteLoginRequest("myUsername")
        Thread.sleep(300)

        assertEquals(kernel.sessionCount, 1)

        client ! RemoteLogoutRequest()
        Thread.sleep(300)

        assertEquals(kernel.sessionCount, 0)
    }

    @Test
    def testDocumentCount: Unit = {
        client ! RemoteLoginRequest("myUsername")
        client ! RemoteNewDocumentRequest("title")
        Thread.sleep(300)

        assertEquals(kernel.documentCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 1)

        client ! RemoteLogoutRequest()
        Thread.sleep(300)

        assertEquals(kernel.documentCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 0)
    }

    @Test
    def test2Clients: Unit = {
        val client2 = new ClientActor(ka, NullPeer)
        client2.converter = converter
        client2.start

        var docList: List[String] = null

        client2 ! NetworkActors(actor {
            loop {
                receive {
                    case RemoteDocumentListResponse(doc) => {
                        println("OKKK")
                        docList = doc
                    }
                    case a: Any => {println("aaaaa" + a)}
                }
            }
        }, remoteEchoClient)

        client ! RemoteLoginRequest("myUsername")
        client ! RemoteNewDocumentRequest("title")
        client2 ! RemoteLoginRequest("myUsername2")
        client2 ! RemoteDocumentListRequest()

        Thread.sleep(1000)

        assertEquals(docList, List("title"))
        assertEquals(kernel.sessionCount, 2)
    }

    def calculateSubscriberCount(count: Option[Future[Int]]) = {
        Thread.sleep(300)
        count.get.apply
    }
}
