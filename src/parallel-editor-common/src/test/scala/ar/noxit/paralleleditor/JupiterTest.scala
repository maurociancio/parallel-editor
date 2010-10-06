package ar.noxit.paralleleditor

import common.{Message, JupiterSynchronizer}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit

@Test
class JupiterTest extends AssertionsForJUnit {

    @Test
    def test {
        val js = new JupiterSynchronizer
        js.generateMsg("c1")
        js.generateMsg("c2")

        js.receiveMsg(Message("s1", 0, 0))
        println(js.outgoingMsgs)
    }
}
