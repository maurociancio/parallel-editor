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
package ar.noxit.paralleleditor.common.operation

class AddTextOperation(val text: String, val startPos: Int, val pword: List[Int] = List()) extends EditOperation {
    def executeOn(documentData: DocumentData) = {
        // caret position
        val caret = documentData.caret
        val caretOffset = caret.offset
        val selectionLength = caret.selectionLength

        // insert the text
        documentData.replace(startPos, 0, text)

        // selection range
        val selectedRange = (caretOffset + 1) until (caretOffset + selectionLength)
        if (selectedRange contains startPos) {
            caret.change(caretOffset, selectionLength + text.size)
        } else {
            if (caretOffset < startPos)
                caret.change(caretOffset, selectionLength)
            else
                caret.change(caretOffset + text.size, selectionLength)
        }
    }

    override def toString = "AddTextOperation pos=%d t=%s p=%s".format(startPos, text, pword)

    override def equals(obj: Any) =
        obj match {
            case other: AddTextOperation =>
                text == other.text && startPos == other.startPos && pword == other.pword
            case _ => false
        }
}
