package ar.noxit.paralleleditor.gui

import javax.swing.event.{DocumentListener, DocumentEvent}
import swing.EditorPane
import swing.event.Event

case class TextAdded(val initPos: Int, val length: Int) extends Event
case class TextRemoved(val initPos: Int, val length: Int) extends Event

class NotificationEditPane extends EditorPane {
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
}
