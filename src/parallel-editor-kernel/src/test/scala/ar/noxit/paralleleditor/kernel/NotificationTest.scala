package ar.noxit.paralleleditor.kernel

import actors.converter.DefaultRemoteMessageConverter
import actors.{ClientActor, KernelActor}
import basic.BasicKernel
import messages.{UserLoggedOut, NewUserLoggedIn}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._
import ar.noxit.paralleleditor.common.network.SenderActor
import ar.noxit.paralleleditor.common.remote.{NetworkActors, Peer}
import ar.noxit.paralleleditor.common.messages.{RemoteNewUserLoggedIn, RemoteLoginOkResponse, RemoteLoginRequest}

@Test
class NotificationTest extends AssertionsForJUnit {
    var kernel: BasicKernel = _

    @Before
    def setUp: Unit = {
        kernel = new BasicKernel
        kernel.timeout = 5000
    }

    @Test
    def testNotificationAtLogin: Unit = {
        val callback1 = createMock(classOf[UpdateCallback])
        callback1 update NewUserLoggedIn("username2")
        replay(callback1)

        val session1 = kernel.login("username1")
        session1.installOnUpdateCallback(callback1)

        val session2 = kernel.login("username2")

        verify(callback1)
    }

    @Test
    def testNotificationAtLoginAndLogout: Unit = {
        val callback1 = createMock(classOf[UpdateCallback])
        callback1 update NewUserLoggedIn("username2")
        callback1 update UserLoggedOut("username2")
        replay(callback1)

        val session1 = kernel.login("username1")
        session1.installOnUpdateCallback(callback1)

        val session2 = kernel.login("username2")
        session2.logout

        verify(callback1)
    }

    @Test
    def testNotificationAtLoginAndLogoutWithMoreThanOneUser: Unit = {
        val callback1 = createMock(classOf[UpdateCallback])
        callback1 update NewUserLoggedIn("username2")
        callback1 update NewUserLoggedIn("username3")
        callback1 update NewUserLoggedIn("username4")
        callback1 update UserLoggedOut("username2")
        callback1 update UserLoggedOut("username3")
        callback1 update UserLoggedOut("username4")
        replay(callback1)

        val session1 = kernel.login("username1")
        session1.installOnUpdateCallback(callback1)

        val session2 = kernel.login("username2")
        val session3 = kernel.login("username3")
        val session4 = kernel.login("username4")
        session2.logout
        session3.logout
        session4.logout

        verify(callback1)
    }

    object NullPeer extends Peer {
        def disconnect = {}
    }

    @Test
    def testNotificationWithActors: Unit = {
        val ka = new KernelActor(kernel)
        ka.start

        // cliente 1
        val gateway1 = createStrictMock(classOf[SenderActor])
        gateway1 ! RemoteLoginOkResponse()
        gateway1 ! RemoteNewUserLoggedIn("username2")
        replay(gateway1)

        val client1 = new ClientActor(ka, NullPeer)
        client1.remoteConverter = new DefaultRemoteMessageConverter
        client1.start

        client1 ! NetworkActors(gateway1, null)
        client1 ! RemoteLoginRequest("username1")

        // cliente 2
        val gateway2 = createStrictMock(classOf[SenderActor])
        gateway2 ! RemoteLoginOkResponse()
        replay(gateway2)

        Thread.sleep(1000)
        val client2 = new ClientActor(ka, NullPeer)
        client2.remoteConverter = new DefaultRemoteMessageConverter
        client2.start

        client2 ! NetworkActors(gateway2, null)
        client2 ! RemoteLoginRequest("username2")

        Thread.sleep(1000)
        verify(gateway1, gateway2)
    }
}
