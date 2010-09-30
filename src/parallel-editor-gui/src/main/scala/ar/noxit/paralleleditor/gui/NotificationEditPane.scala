package ar.noxit.paralleleditor.gui

import javax.swing.event.{DocumentListener, DocumentEvent}
import swing.EditorPane
import swing.event.Event
import javax.swing.text.PlainDocument

case class TextAdded(val initPos: Int, val length: Int) extends Event
case class TextRemoved(val initPos: Int, val length: Int) extends Event

class NotificationEditPane extends EditorPane {
    private var fireEvents = true
    private val doc = new PlainDocument {
        override def fireRemoveUpdate(e: DocumentEvent) = if (fireEvents) super.fireRemoveUpdate(e)

        override def fireInsertUpdate(e: DocumentEvent) = if (fireEvents) super.fireInsertUpdate(e)
    }
    peer.setDocument(doc)

    peer.getDocument.addDocumentListener(new DocumentListener {
        def changedUpdate(e: DocumentEvent) {
        }

        def insertUpdate(e: DocumentEvent) {
            publish(TextAdded(e.getOffset, e.getLength))
        }

        def removeUpdate(e: DocumentEvent) {
            publish(TextRemoved(e.getOffset, e.getLength))
        }
    })

    def disableFiringEvents {
        this.fireEvents = false;
    }

    def enableFiringEvents {
        this.fireEvents = true;
    }
}
