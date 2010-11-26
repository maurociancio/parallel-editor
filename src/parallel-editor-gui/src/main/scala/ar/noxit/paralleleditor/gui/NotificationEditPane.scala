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
package ar.noxit.paralleleditor.gui

import javax.swing.event.{DocumentListener, DocumentEvent}
import javax.swing.text.PlainDocument
import swing.TextArea

class NotificationEditPane extends TextArea {
    private var fireEvents = true

    private val doc = new PlainDocument {
        override def fireRemoveUpdate(e: DocumentEvent) = if (fireEvents) super.fireRemoveUpdate(e)

        override def fireInsertUpdate(e: DocumentEvent) = if (fireEvents) super.fireInsertUpdate(e)
    }

    doc.addDocumentListener(new DocumentListener {
        def changedUpdate(e: DocumentEvent) {
        }

        def insertUpdate(e: DocumentEvent) {
            val newText = text.substring(e.getOffset, e.getOffset + e.getLength)
            val ie = InsertionEvent(e.getOffset, newText)

            publicar(ie)
        }

        def removeUpdate(e: DocumentEvent) {
            val de = DeletionEvent(e.getOffset, e.getLength)
            publicar(de)
        }
    })
    peer.setDocument(doc)

    protected def publicar(e: EditionEvent): Unit = {
        publish(WrappedEvent(e))
    }

    def disableFiringEvents {
        this.fireEvents = false;
    }

    def enableFiringEvents {
        this.fireEvents = true;
    }
}
