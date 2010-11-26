/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
     * "TEXTOLARGO"
     *   |||||||||^
     * "TEXTOLARGOnuevo"
     *   |||||||||^
     */
    @Test
    def testInsertAfterSelection: Unit = {
        val ato = new AddTextOperation("nuevo", 10)

        val doc = newDocument("TEXTOLARGO", offset = 1, selectionLen = 9)
        ato.executeOn(doc)

        Assert.assertEquals("TEXTOLARGOnuevo", doc.data)
        Assert.assertEquals(1, doc.caret.offset)
        Assert.assertEquals(9, doc.caret.selectionLength)
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
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "IGINAL"
     *  |^
     */
    @Test
    def testDeleteText2: Unit = {
        val dto = new DeleteTextOperation(startPos = 0, size = 2)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("IGINAL", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(1, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "GINAL"
     *  ^
     */
    @Test
    def testDeleteText3: Unit = {
        val dto = new DeleteTextOperation(startPos = 0, size = 3)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("GINAL", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(0, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "INAL"
     *  ^
     */
    @Test
    def testDeleteText4: Unit = {
        val dto = new DeleteTextOperation(startPos = 0, size = 4)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("INAL", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(0, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "ORIGI"
     *   ||^
     */
    @Test
    def testDeleteText5: Unit = {
        val dto = new DeleteTextOperation(startPos = 5, size = 3)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("ORIGI", doc.data)
        Assert.assertEquals(1, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "ORIG"
     *   ||^
     */
    @Test
    def testDeleteText6: Unit = {
        val dto = new DeleteTextOperation(startPos = 4, size = 4)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("ORIG", doc.data)
        Assert.assertEquals(1, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "ORI"
     *   ||^
     */
    @Test
    def testDeleteText7: Unit = {
        val dto = new DeleteTextOperation(startPos = 3, size = 5)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("ORI", doc.data)
        Assert.assertEquals(1, doc.caret.offset)
        Assert.assertEquals(2, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "OR"
     *   |^
     */
    @Test
    def testDeleteText8: Unit = {
        val dto = new DeleteTextOperation(startPos = 2, size = 6)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("OR", doc.data)
        Assert.assertEquals(1, doc.caret.offset)
        Assert.assertEquals(1, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * "O"
     *   ^
     */
    @Test
    def testDeleteText9: Unit = {
        val dto = new DeleteTextOperation(startPos = 1, size = 7)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("O", doc.data)
        Assert.assertEquals(1, doc.caret.offset)
        Assert.assertEquals(0, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *   ||^
     * ""
     *  ^
     */
    @Test
    def testDeleteText10: Unit = {
        val dto = new DeleteTextOperation(startPos = 0, size = 8)

        val doc = newDocument("ORIGINAL", offset = 1, selectionLen = 2)
        dto.executeOn(doc)

        Assert.assertEquals("", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(0, doc.caret.selectionLength)
    }

    /**
     * "ORIGINAL"
     *  ||||||||^
     * "ORINAL"
     *  ||||||^
     */
    @Test
    def testDeleteText11: Unit = {
        val dto = new DeleteTextOperation(startPos = 2, size = 2)

        val doc = newDocument("ORIGINAL", offset = 0, selectionLen = 8)
        dto.executeOn(doc)

        Assert.assertEquals("ORINAL", doc.data)
        Assert.assertEquals(0, doc.caret.offset)
        Assert.assertEquals(6, doc.caret.selectionLength)
    }

    /**
     * ""
     *  ^
     * "text"
     *  ^
     */
    @Test
    def testInsertEmptyText: Unit = {
        val dto = new AddTextOperation(text = "text", startPos = 0)

        val doc = newDocument("", offset = 0, selectionLen = 0)
        dto.executeOn(doc)

        Assert.assertEquals("text", doc.data)
        Assert.assertEquals(4, doc.caret.offset)
        Assert.assertEquals(0, doc.caret.selectionLength)
    }

    def newDocument(text: String, offset: Int, selectionLen: Int) =
        new BaseDocumentData(text) {
            val caret = new BaseCaret(offset, selectionLen)
        }

    abstract class BaseDocumentData(var data: String) extends DocumentData {
        def replace(offset: Int, length: Int, newText: String) =
            data = data.substring(0, offset) + (if (newText == null) "" else newText) + "" + data.substring(offset + length)
    }
    class BaseCaret(var _offset: Int, var _selectionLength: Int) extends Caret {
        override def selectionLength = _selectionLength

        override def offset = _offset

        override def change(offset: Int, selectionLength: Int) = {
            _offset = offset;
            _selectionLength = selectionLength
        }
    }
}
