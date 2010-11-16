package ar.noxit.paralleleditor

import common.operation.{DeleteTextOperation, Caret, DocumentData, AddTextOperation}
import org.scalatest.junit.AssertionsForJUnit
import org.junit.{Assert, Test}

@Test
class CaretTest extends AssertionsForJUnit {
    /**
     * "ORIGINAL"
     *  ^  caret
     * =>
     * "textORIGINAL"
     *      ^ caret
     */
    @Test
    def testPositionCaretInSameInsertPosition: Unit = {
        val ato = new AddTextOperation("text", 0)

        val doc = newDocument("ORIGINAL", 0, 0)
        ato.executeOn(doc)

        Assert.assertEquals("textORIGINAL", doc.data)
        Assert.assertEquals(4, doc.caret.offset)
        Assert.assertEquals(0, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *  |^ caret + selection
     *
     * "ORtextIGINAL"
     *  |^ caret + selection
     */
    @Test
    def testPositionCaretAfterInsert: Unit = {
        val ato = new AddTextOperation("text", 2)

        val doc = newDocument("ORIGINAL", offset = 0, selectionLen = 1)
        ato.executeOn(doc)

        Assert.assertEquals("ORtextIGINAL", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(1, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *  ||^
     * =>
     * "ORtextIGINAL"
     *  ||^
     */
    @Test
    def testPositionCaretAfterInsert2: Unit = {
        val ato = new AddTextOperation("text", 2)

        val doc = newDocument("ORIGINAL", offset = 0, selectionLen = 2)
        ato.executeOn(doc)

        Assert.assertEquals("ORtextIGINAL", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *    ||||^
     * =>
     * "ORIGtextINAL"
     *    ||||||||^
     */
    @Test
    def testPositionCaretInMiddleOfInsert: Unit = {
        val ato = new AddTextOperation("text", 4)

        val doc = newDocument("ORIGINAL", offset = 2, selectionLen = 4)
        ato.executeOn(doc)

        Assert.assertEquals("ORIGtextINAL", doc.data)
        Assert.assertEquals(2, doc.caret.offset)
        Assert.assertEquals(8, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "ORIGtextINAL"
     *   ||^
     */
    @Test
    def testPositionCaretInMiddleOfInsert2: Unit = {
        val ato = new AddTextOperation("text", 4)

        val doc = newDocument("ORIGINAL", offset = 2, selectionLen = 2)
        ato.executeOn(doc)

        Assert.assertEquals("ORIGtextINAL", doc.data)
        Assert.assertEquals(2, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *      ||^
     * "ORIGtextINAL"
     *          ||^
     */
    @Test
    def testPositionCaretInMiddleOfInsert3: Unit = {
        val ato = new AddTextOperation("text", 4)

        val doc = newDocument("ORIGINAL", offset = 4, selectionLen = 2)
        ato.executeOn(doc)

        Assert.assertEquals("ORIGtextINAL", doc.data)
        Assert.assertEquals(8, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *      ||^
     * "ORItextGINAL"
     *          ||^
     */
    @Test
    def testPositionCaretInMiddleOfInsert4: Unit = {
        val ato = new AddTextOperation("text", 3)

        val doc = newDocument("ORIGINAL", offset = 4, selectionLen = 2)
        ato.executeOn(doc)

        Assert.assertEquals("ORItextGINAL", doc.data)
        Assert.assertEquals(8, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "RIGINAL"
     *  ||^
     */
    @Test
    def testDeleteText: Unit = {
        val dto = new DeleteTextOperation(startPos = 0, size = 1)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("RIGINAL", doc.data)
//        Assert.assertEquals(0, doc.caret.offset)
//        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    def newDocument(text: String, offset: Int, selectionLen: Int) =
        new BaseDocumentData(text) {
            val caret = new BaseCaret(offset, selectionLen)
        }

    abstract class BaseDocumentData(var data: String) extends DocumentData
    class BaseCaret(var _offset: Int, var _selectionLength: Int) extends Caret {
        override def selectionLength = _selectionLength

        override def offset = _offset

        override def change(offset: Int, selectionLength: Int) = {
            _offset = offset;
            _selectionLength = selectionLength
        }
    }
}
