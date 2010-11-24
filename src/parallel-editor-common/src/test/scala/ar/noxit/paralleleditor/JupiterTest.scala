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

        applyAndGenerate(applyClient, new AddTextOperation("H", 0), js)
        assertEquals(ctext.data, "Habcdefg")

        js.receive(Message(new AddTextOperation("D", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HaDbcdefg")
        js.receive(Message(new AddTextOperation("C", 8), 1, 0), applyClient)
        assertEquals(ctext.data, "HaDbcdefgC")
    }

    @Test
    def testJupiterWithDeletes {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        applyAndGenerate(applyClient, new AddTextOperation("H", 0), js)
        assertEquals(ctext.data, "Habcdefg")

        js.receive(Message(new AddTextOperation("D", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HaDbcdefg")

        js.receive(Message(new DeleteTextOperation(5, 1), 1, 0), applyClient)
        assertEquals(ctext.data, "HaDbcdfg")
    }

    @Test
    def testJupiterWithDeletes2 {
        val (ctext, stext) = docFromText("abcdefg")
        val applyClient: EditOperation => Unit = {_.executeOn(ctext)}

        val js = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

        applyAndGenerate(applyClient, new AddTextOperation("H", 0), js)
        assertEquals(ctext.data, "Habcdefg")

        js.receive(Message(new AddTextOperation("D", 1), 0, 0), applyClient)
        assertEquals(ctext.data, "HaDbcdefg")

        js.receive(Message(new DeleteTextOperation(0, 1), 1, 0), applyClient)
        assertEquals(ctext.data, "HDbcdefg")
    }

    def docFromText(text: String) =
        (new MockDocument(text), new MockDocument(text))
}
