package ar.noxit.paralleleditor.kernel

import basic.BasicKernel
import messages.ChatMessage
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._

class ChatTest extends AssertionsForJUnit {
    @Before
    def setUp = {
    }

    @Test
    def testChatBetweenTwoUsers: Unit = {
        val k = new BasicKernel

        val s1 = k.login("username1")
        val s2 = k.login("username2")

        val cb1 = createMock(classOf[UpdateCallback])
        val cb2 = createMock(classOf[UpdateCallback])
        cb2 update ChatMessage(s1, "hello there")
        cb1 update ChatMessage(s2, "hello again")
        replay(cb1, cb2)

        s1.installOnUpdateCallback(cb1)
        s2.installOnUpdateCallback(cb2)

        k.chat(s1, "hello there")
        k.chat(s2, "hello again")

        verify(cb1, cb2)
    }
}
