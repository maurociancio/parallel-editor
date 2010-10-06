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
