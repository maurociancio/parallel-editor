package ar.noxit.paralleleditor.kernel

import basic.BasicKernel
import messages.{UserLoggedOut, NewUserLoggedIn}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._

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
}
