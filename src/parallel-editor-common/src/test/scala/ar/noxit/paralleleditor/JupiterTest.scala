package ar.noxit.paralleleditor

import common._
import operation.{DeleteTextOperation, EditOperation, AddTextOperation, DocumentData}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import Assert._

@Test
class JupiterTest extends AssertionsForJUnit {

    def applyAndGenerate(applyClient: (EditOperation) => Unit, o: EditOperation, js: EditOperationJupiterSynchronizer) {
        applyClient(o)
        js.generate(o, op => {})
    }

    @Test
    def testJupiter {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        applyAndGenerate(applyClient, new AddTextOperation("HOLA", 0), js)
        assertEquals(ctext.data, "HOLAabcdefg")

        js.receive(Message(new AddTextOperation("DOG", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefg")
        js.receive(Message(new AddTextOperation("CAT", 10), 1, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefgCAT")
    }

    @Test
    def testJupiterWithDeletes {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        applyAndGenerate(applyClient, new AddTextOperation("HOLA", 0), js)
        assertEquals(ctext.data, "HOLAabcdefg")

        js.receive(Message(new AddTextOperation("DOG", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefg")

        js.receiveMsg(Message(new DeleteTextOperation(7, 1), 1, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdfg")
    }

    @Test
    def testJupiterWithDeletes2 {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        applyAndGenerate(applyClient, new AddTextOperation("HOLA", 0), js)
        assertEquals(ctext.data, "HOLAabcdefg")

        js.receive(Message(new AddTextOperation("DOG", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HOLAaDOGbcdefg")

        js.receiveMsg(Message(new DeleteTextOperation(0, 1), 1, 0), applyClient)
        assertEquals(ctext.data, "HOLADOGbcdefg")
    }

    def docFromText(text: String) = (new DocumentData {var data = text}, new DocumentData {var data = text})
}
