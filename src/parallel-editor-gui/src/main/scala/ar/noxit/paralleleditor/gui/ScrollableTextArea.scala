package ar.noxit.paralleleditor.gui

import scala.swing._

class ScrollableTextArea extends SplitPane with ConcurrentDocument {
    
    val areaEdicion = new NotificationEditPane {
        text = ""
    }

    val scrollAreaEdicion = new ScrollPane(areaEdicion)

    scrollAreaEdicion preferredSize = new Dimension(320, 240)

    val debugConsole = new TextArea {
        text = "-- debug console --\n"
        editable = false
    }

    val scrollDebugConsole = new ScrollPane(debugConsole)

    orientation = Orientation.Horizontal
    leftComponent = scrollAreaEdicion
    rightComponent = scrollDebugConsole
    oneTouchExpandable = true


    listenTo(areaEdicion)
    
    reactions += {
        case TextAdded(initPos, length) => {
            val newText = areaEdicion.text.substring(initPos, initPos + length)
            addEntry("text added '%s' at pos: %d - size: %d".format(newText, initPos, length))
            publish(InsertionEvent(initPos, newText))
        }
        case TextRemoved(initPos, length) => {
            addEntry("text removed at pos: %d - size: %d".format(initPos, length))
            publish(DeletionEvent(initPos, length))
        }
    }

    def addEntry(msg: String) {
        debugConsole append (msg + '\n')
        debugConsole.caret.position = debugConsole.text.size       
    }

    override def removeText(pos: Int, count: Int) = {
        areaEdicion.disableFiringEvents
        try {
            val text = areaEdicion.text
            areaEdicion.text = text.substring(0, pos) + text.substring(pos + count)
            areaEdicion.repaint
        } finally {
            areaEdicion.enableFiringEvents
        }
    }

    override def addText(pos: Int, text: String) = {
        areaEdicion.disableFiringEvents
        try {
            val original = areaEdicion.text
            areaEdicion.text = original.substring(0, pos) + text + original.substring(pos)
            areaEdicion.repaint
        } finally {
            areaEdicion.enableFiringEvents
        }
    }
}
