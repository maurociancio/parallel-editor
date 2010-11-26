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

import ar.noxit.paralleleditor.common.BasicXFormStrategy
import common.operation._
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

        val c = new AddTextOperation("a", 1)
        val s = new AddTextOperation("d", 1)

        checkTransform(c, ctext, s, stext, "xadyz")
    }

    @Test
    def testAddTextEqualPosition3 {
        val (ctext, stext) = docFromText("")

        val c = new AddTextOperation("a", 0)
        val s = new AddTextOperation("b", 0)

        checkTransform(c, ctext, s, stext, "ab")
    }

    @Test
    def testAddTextEqualPosition4 {
        val (ctext, stext) = docFromText("")

        val c = new AddTextOperation("a", 0)
        val s = new AddTextOperation("b", 0)

        val (cprima, sprima) = xf.xform(c, s)

        // c y s' en cliente
        c.executeOn(ctext)
        sprima.executeOn(ctext)

        // s y c' en servidor
        s.executeOn(stext)
        cprima.executeOn(stext)

        assertEquals(ctext.data, stext.data)
    }

    // c.pos < s.pos
    @Test
    def testAddTextDifferentPosition1 {
        val (ctext, stext) = docFromText("xyz")

        val c = new AddTextOperation("a", 1)
        val s = new AddTextOperation("d", 2)

        checkTransform(c, ctext, s, stext, "xaydz")
    }

    //c.pos > s.pos
    @Test
    def testAddTextDifferentPosition2 {
        val (ctext, stext) = docFromText("xyz")

        val c = new AddTextOperation("a", 2)
        val s = new AddTextOperation("d", 1)

        checkTransform(c, ctext, s, stext, "xdyaz")
    }

    @Test
    def testDeleteTextEqualPosition1 {
        val (ctext, stext) = docFromText("abcde")

        val c = new DeleteTextOperation(startPos = 1, size = 1)
        val s = new DeleteTextOperation(startPos = 1, size = 1)

        checkTransform(c, ctext, s, stext, "acde")
    }

    @Test
    def testDeleteTextDisjoint {
        val (ctext, stext) = docFromText("abcdefgh")

        val c = new DeleteTextOperation(startPos = 1, size = 1)
        val s = new DeleteTextOperation(startPos = 4, size = 1)

        checkTransform(c, ctext, s, stext, "acdfgh")
    }

    @Test
    def testDeleteTextDisjoint2 {
        val (ctext, stext) = docFromText("abcdefgh")

        val c = new DeleteTextOperation(startPos = 4, size = 1)
        val s = new DeleteTextOperation(startPos = 1, size = 1)

        checkTransform(c, ctext, s, stext, "acdfgh")
    }


    @Test
    def testAddTextDeleteTextDisjoint {
        val (ctext, stext) = docFromText("abcdef")

        val c = new AddTextOperation(startPos = 1, text = "h")
        val s = new DeleteTextOperation(startPos = 3, size = 1)

        checkTransform(c, ctext, s, stext, "ahbcef")
    }

    @Test
    def testAddTextDeleteTextDisjoint2 {
        val (ctext, stext) = docFromText("abcdef")

        val c = new AddTextOperation(startPos = 5, text = "h")
        val s = new DeleteTextOperation(startPos = 1, size = 1)

        checkTransform(c, ctext, s, stext, "acdehf")
    }

    @Test
    def testAddTextDeleteText {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 4, text = "h")
        val s = new DeleteTextOperation(startPos = 2, size = 1)

        checkTransform(c, ctext, s, stext, "abdhefg")
    }

    @Test
    def testAddTextDeleteText2 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 5, text = "h")
        val s = new DeleteTextOperation(startPos = 2, size = 1)

        checkTransform(c, ctext, s, stext, "abdehfg")
    }

    @Test
    def testAddTextDeleteText3 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 6, text = "h")
        val s = new DeleteTextOperation(startPos = 2, size = 1)

        checkTransform(c, ctext, s, stext, "abdefhg")
    }

    @Test
    def testAddTextDeleteText4 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new AddTextOperation(startPos = 7, text = "h")
        val s = new DeleteTextOperation(startPos = 2, size = 1)

        checkTransform(c, ctext, s, stext, "abdefgh")
    }

    @Test
    def testAddTextDeleteText5 {
        val (ctext, stext) = docFromText("abcd")

        val c = new AddTextOperation(startPos = 2, text = "h")
        val s = new DeleteTextOperation(startPos = 2, size = 1)

        checkTransform(c, ctext, s, stext, "abhd")
    }
    // delete add

    @Test
    def testAddTextDeleteTextDisjointInverse {
        val (ctext, stext) = docFromText("abcdef")

        val c = new DeleteTextOperation(startPos = 3, size = 1)
        val s = new AddTextOperation(startPos = 1, text = "h")

        checkTransform(c, ctext, s, stext, "ahbcef")
    }

    @Test
    def testAddTextDeleteTextDisjointInverse2 {
        val (ctext, stext) = docFromText("abcdef")

        val c = new DeleteTextOperation(startPos = 1, size = 1)
        val s = new AddTextOperation(startPos = 5, text = "h")

        checkTransform(c, ctext, s, stext, "acdehf")
    }

    @Test
    def testAddTextDeleteTextInverse {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new DeleteTextOperation(startPos = 2, size = 1)
        val s = new AddTextOperation(startPos = 4, text = "h")

        checkTransform(c, ctext, s, stext, "abdhefg")
    }

    @Test
    def testAddTextDeleteTextInverse2 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new DeleteTextOperation(startPos = 2, size = 1)
        val s = new AddTextOperation(startPos = 5, text = "h")

        checkTransform(c, ctext, s, stext, "abdehfg")
    }

    @Test
    def testAddTextDeleteTextInverse3 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new DeleteTextOperation(startPos = 2, size = 1)
        val s = new AddTextOperation(startPos = 6, text = "h")

        checkTransform(c, ctext, s, stext, "abdefhg")
    }

    @Test
    def testAddTextDeleteTextInverse4 {
        val (ctext, stext) = docFromText("abcdefg")

        val c = new DeleteTextOperation(startPos = 2, size = 1)
        val s = new AddTextOperation(startPos = 7, text = "h")

        checkTransform(c, ctext, s, stext, "abdefgh")
    }

    def checkTransform(c: EditOperation, ctext: DocumentData, s: EditOperation, stext: DocumentData, expected: String) {
        c.executeOn(ctext)
        s.executeOn(stext)

        val to = xf.xform((c, s))

        to._2.executeOn(ctext)
        to._1.executeOn(stext)

        assertEquals(ctext.data, stext.data)
        assertEquals(expected, ctext.data)
    }

    def docFromText(text: String) =
        (new MockDocument(text), new MockDocument(text))
}

class MockDocument(var text: String) extends DocumentData {
    override def data = text
    override def replace(offset: Int, length: Int, newText: String) =
       text = data.substring(0, offset) + (if (newText == null) "" else newText) + "" + data.substring(offset + length)
    val caret = new NullCaret
}
