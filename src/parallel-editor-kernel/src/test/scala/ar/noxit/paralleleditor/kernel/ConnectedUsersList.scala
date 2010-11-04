package ar.noxit.paralleleditor.kernel

import actors.converter.{DefaultRemoteMessageConverter, DefaultToKernelConverter}
import actors.{KernelActor, ClientActor}
import basic.sync.SynchronizerAdapterFactory
import basic.BasicKernel
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock
import ar.noxit.paralleleditor.common.network.SenderActor
import ar.noxit.paralleleditor.common.BasicXFormStrategy
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.remote.{NetworkActors, Peer}

@Test
class ConnectedUsersList extends AssertionsForJUnit {
    @Before
    def setUp = {
    }

    object NullPeer extends Peer {
        def disconnect = {}
    }

    @Test
    def testConnectedUser: Unit = {
        val k = new BasicKernel
        k.timeout = 5

        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        k.sync = synchronizerAdapterFactory

        val ka = new KernelActor(k)
        ka.start

        val gateway = EasyMock.createStrictMock(classOf[SenderActor])
        gateway ! RemoteLoginOkResponse()
        gateway ! RemoteDocumentSubscriptionResponse("new_doc", "content")
        gateway ! RemoteUserListResponse(Map(("username1", List[String]())))
        EasyMock.replay(gateway)

        val ca = new ClientActor(ka, NullPeer)
        ca.toKernelConverter = new DefaultToKernelConverter
        ca.remoteConverter = new DefaultRemoteMessageConverter
        ca.start
        ca ! NetworkActors(gateway, null)
        ca ! RemoteLoginRequest("username1")
        ca ! RemoteNewDocumentRequest("new_doc", "content")
        ca ! RemoteUserListRequest()

        Thread.sleep(500)
        EasyMock.verify(gateway)
    }
}
