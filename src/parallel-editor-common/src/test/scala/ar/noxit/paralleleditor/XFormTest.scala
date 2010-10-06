package ar.noxit.paralleleditor

import ar.noxit.paralleleditor.common.BasicXFormStrategy
import common.operation.{DeleteTextOperation, EditOperation, DocumentData, AddTextOperation}
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

    @Test
    def testDeleteTextEqualPosition1 {
        val (ctext, stext) = docFromText("abcde")

        val c = new DeleteTextOperation(startPos = 1, size = 1)
        val s = new DeleteTextOperation(startPos = 1, size = 1)

        checkTransform(c, ctext, s, stext, "acde")
    }

    @Test
    def testDeleteTextEqualPosition2 {
        val (ctext, stext) = docFromText("abcde")

        val c = new DeleteTextOperation(startPos = 1, size = 2)
        val s = new DeleteTextOperation(startPos = 1, size = 1)

        checkTransform(c, ctext, s, stext, "ade")
    }

    @Test
    def testDeleteTextEqualPosition3 {
        val (ctext, stext) = docFromText("abcde")

        val c = new DeleteTextOperation(startPos = 1, size = 1)
        val s = new DeleteTextOperation(startPos = 1, size = 2)

        checkTransform(c, ctext, s, stext, "ade")
    }

    @Test
    def testDeleteTextEqualPosition4 {
        val (ctext, stext) = docFromText("abcdefgh")

        val c = new DeleteTextOperation(startPos = 1, size = 4)
        val s = new DeleteTextOperation(startPos = 3, size = 3)

        checkTransform(c, ctext, s, stext, "agh")
    }

    @Test
    def testDeleteTextDisjoint {
        val (ctext, stext) = docFromText("abcdefgh")

        val c = new DeleteTextOperation(startPos = 1, size = 2)
        val s = new DeleteTextOperation(startPos = 4, size = 3)

        checkTransform(c, ctext, s, stext, "adh")
    }

    @Test
    def testDeleteTextDisjoint3 {
        val (ctext, stext) = docFromText("abcdefgh")

        val c = new DeleteTextOperation(startPos = 4, size = 3)
        val s = new DeleteTextOperation(startPos = 1, size = 2)

        checkTransform(c, ctext, s, stext, "adh")
    }

    @Test
    def testDeleteTextDisjoint2 {
        val (ctext, stext) = docFromText("abcdefgh")

        val c = new DeleteTextOperation(startPos = 4, size = 3)
        val s = new DeleteTextOperation(startPos = 1, size = 2)

        checkTransform(c, ctext, s, stext, "adh")
    }

    @Test
    def testAddTextDeleteTextDisjoint {
        val (ctext, stext) = docFromText("abcdef")

        val c = new AddTextOperation(startPos = 1, text = "hola")
        val s = new DeleteTextOperation(startPos = 3, size = 2)

        checkTransform(c, ctext, s, stext, "aholabcf")
    }

    @Test
    def testAddTextDeleteTextDisjoint2 {
        val (ctext, stext) = docFromText("abcdef")

        val c = new AddTextOperation(startPos = 5, text = "hola")
        val s = new DeleteTextOperation(startPos = 1, size = 2)

        checkTransform(c, ctext, s, stext, "adeholaf")
    }

    @Test
    def testAddTextDeleteText {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 4, text = "hola")
        val s = new DeleteTextOperation(startPos = 2, size = 4)

        checkTransform(c, ctext, s, stext, "abholag")
    }

    @Test
    def testAddTextDeleteText2 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 5, text = "hola")
        val s = new DeleteTextOperation(startPos = 2, size = 4)

        checkTransform(c, ctext, s, stext, "abholag")
    }

    @Test
    def testAddTextDeleteText3 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 6, text = "hola")
        val s = new DeleteTextOperation(startPos = 2, size = 4)

        checkTransform(c, ctext, s, stext, "abholag")
    }

    @Test
    def testAddTextDeleteText4 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 7, text = "hola")
        val s = new DeleteTextOperation(startPos = 2, size = 4)

        checkTransform(c, ctext, s, stext, "abghola")
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
