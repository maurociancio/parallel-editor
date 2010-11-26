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
