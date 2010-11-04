package ar.noxit.paralleleditor.kernel

import actors.converter.{DefaultRemoteMessageConverter, DefaultToKernelConverter}
import actors.{KernelActor, ClientActor}
import basic.sync.SynchronizerAdapterFactory
import basic.BasicKernel
import basic.userlist.DefaultUserListMerger
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock
import ar.noxit.paralleleditor.common.network.SenderActor
import ar.noxit.paralleleditor.common.BasicXFormStrategy
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.remote.{NetworkActors, Peer}

@Test
class ConnectedUsersList extends AssertionsForJUnit {

    var k: BasicKernel = _
    var ka: KernelActor = _

    @Before
    def setUp = {
        k = new BasicKernel
        val merger = new DefaultUserListMerger
        merger.timeout = 5000
        k.userListMerger = merger
        k.timeout = 5000

        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        k.sync = synchronizerAdapterFactory

        ka = new KernelActor(k)
    }

    object NullPeer extends Peer {
        def disconnect = {}
    }

    @Test
    def testConnectedUser: Unit = {
        ka.start

        val gateway = EasyMock.createStrictMock(classOf[SenderActor])
        gateway ! RemoteLoginOkResponse()
        gateway ! RemoteDocumentSubscriptionResponse("new_doc", "content")
        gateway ! RemoteUserListResponse(Map(("username1", List("new_doc"))))
        EasyMock.replay(gateway)

        val ca = new ClientActor(ka, NullPeer)
        ca.toKernelConverter = new DefaultToKernelConverter
        ca.remoteConverter = new DefaultRemoteMessageConverter
        ca.start
        ca ! NetworkActors(gateway, null)
        ca ! RemoteLoginRequest("username1")
        ca ! RemoteNewDocumentRequest("new_doc", "content")

        Thread.sleep(2000)
        ca ! RemoteUserListRequest()

        Thread.sleep(2000)
        EasyMock.verify(gateway)
    }

    @Test
    def testConnected2User: Unit = {
        ka.start

        // cliente 1
        val gateway1 = EasyMock.createStrictMock(classOf[SenderActor])
        gateway1 ! RemoteLoginOkResponse()
        gateway1 ! RemoteDocumentSubscriptionResponse("new_doc", "content")
        EasyMock.replay(gateway1)

        val ca1 = new ClientActor(ka, NullPeer)
        ca1.toKernelConverter = new DefaultToKernelConverter
        ca1.remoteConverter = new DefaultRemoteMessageConverter
        ca1.start
        ca1 ! NetworkActors(gateway1, null)
        ca1 ! RemoteLoginRequest("username1")
        ca1 ! RemoteNewDocumentRequest("new_doc", "content")

        // cliente 2
        val gateway2 = EasyMock.createStrictMock(classOf[SenderActor])
        gateway2 ! RemoteLoginOkResponse()
        gateway2 ! RemoteDocumentSubscriptionResponse("new_doc", "content")
        gateway2 ! RemoteUserListResponse(Map(("username1", List("new_doc")), ("username2", List("new_doc"))))
        EasyMock.replay(gateway2)

        val ca2 = new ClientActor(ka, NullPeer)
        ca2.toKernelConverter = new DefaultToKernelConverter
        ca2.remoteConverter = new DefaultRemoteMessageConverter
        ca2.start

        ca2 ! NetworkActors(gateway2, null)
        ca2 ! RemoteLoginRequest("username2")

        Thread.sleep(500)

        ca2 ! RemoteSubscribeRequest("new_doc")

        Thread.sleep(500)
        ca2 ! RemoteUserListRequest()

        Thread.sleep(500)
        EasyMock.verify(gateway1)
        EasyMock.verify(gateway2)
    }
}
