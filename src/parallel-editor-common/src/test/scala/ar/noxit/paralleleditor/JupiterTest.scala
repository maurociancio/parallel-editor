package ar.noxit.paralleleditor

import common._
import operation.{DeleteTextOperation, EditOperation, AddTextOperation, DocumentData}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import Assert._

@Test
class JupiterTest extends AssertionsForJUnit {
    @Test
    def testJupiter {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        js.generateMsg(new AddTextOperation("HOLA", 0), applyClient)
        assertEquals(ctext.data, "HOLAabcdefg")

        js.receiveMsg(Message(new AddTextOperation("DOG", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefg")
        js.receiveMsg(Message(new AddTextOperation("CAT", 10), 1, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefgCAT")
    }

    @Test
    def testJupiterWithDeletes {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        js.generateMsg(new AddTextOperation("HOLA", 0), applyClient)
        assertEquals(ctext.data, "HOLAabcdefg")

        js.receiveMsg(Message(new AddTextOperation("DOG", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefg")
        js.receiveMsg(Message(new DeleteTextOperation(7, 3), 1, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcd")
    }

    @Test
    def testJupiterWithDeletes2 {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        js.generateMsg(new AddTextOperation("HOLA", 0), applyClient)
        assertEquals(ctext.data, "HOLAabcdefg")

        js.receiveMsg(Message(new AddTextOperation("DOG", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefg")
        js.receiveMsg(Message(new DeleteTextOperation(0, 10), 1, 0), applyClient)
        assertEquals(ctext.data, "HOLA")
    }

    def docFromText(text: String) = (new DocumentData {var data = text}, new DocumentData {var data = text})
}
