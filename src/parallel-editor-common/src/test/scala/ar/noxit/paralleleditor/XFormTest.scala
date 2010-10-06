package ar.noxit.paralleleditor

import ar.noxit.paralleleditor.common.BasicXFormStrategy
import common.operation.{EditOperation, DocumentData, AddTextOperation}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import Assert._

@Test
class XFormTest extends AssertionsForJUnit {
    private var xf: BasicXFormStrategy = _

    @Before
    def before {
        xf = new BasicXFormStrategy
    }

    @Test
    def testAddTextEqualPosition {
        val (ctext, stext) = docFromText("xyz")

        val c = new AddTextOperation("abc", 1)
        val s = new AddTextOperation("def", 1)

        checkTransform(c, ctext, s, stext, "xabcdefyz")
    }

    // c.pos < s.pos
    @Test
    def testAddTextDifferentPosition1 {
        val (ctext, stext) = docFromText("xyz")

        val c = new AddTextOperation("abc", 1)
        val s = new AddTextOperation("def", 2)

        checkTransform(c, ctext, s, stext, "xabcydefz")
    }

    //c.pos > s.pos
    @Test
    def testAddTextDifferentPosition2 {
        val (ctext, stext) = docFromText("xyz")

        val c = new AddTextOperation("abc", 2)
        val s = new AddTextOperation("def", 1)

        checkTransform(c, ctext, s, stext, "xdefyabcz")
    }

    def checkTransform(c: EditOperation, ctext: DocumentData, s: EditOperation, stext: DocumentData, expected: String) {
        c.executeOn(ctext)
        s.executeOn(stext)

        val to = xf.xform((c, s))

        to._2.executeOn(ctext)
        to._1.executeOn(stext)

        assertEquals(ctext.data, stext.data)
        assertEquals(ctext.data, expected)
    }


    def docFromText(text: String) = (new DocumentData {var data = text}, new DocumentData {var data = text})
}
