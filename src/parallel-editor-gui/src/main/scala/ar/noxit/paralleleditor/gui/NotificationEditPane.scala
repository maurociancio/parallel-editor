package ar.noxit.paralleleditor.gui

import javax.swing.event.{DocumentListener, DocumentEvent}
import swing.event.Event
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
            publish(InsertionEvent(e.getOffset, newText))
        }

        def removeUpdate(e: DocumentEvent) {
            publish(DeletionEvent(e.getOffset, e.getLength))
        }
    })
    peer.setDocument(doc)

    def disableFiringEvents {
        this.fireEvents = false;
    }

    def enableFiringEvents {
        this.fireEvents = true;
    }
}
