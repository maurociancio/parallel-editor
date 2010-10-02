package ar.noxit.paralleleditor.gui

import javax.swing.event.{DocumentListener, DocumentEvent}
import swing.event.Event
import javax.swing.text.PlainDocument
import swing.{TextArea, EditorPane}

case class TextAdded(initPos: Int, length: Int) extends Event
case class TextRemoved(initPos: Int, length: Int) extends Event

class NotificationEditPane extends TextArea {
    //this.contentType = "text/plain"
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
