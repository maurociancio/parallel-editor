package ar.noxit.paralleleditor.gui

import scala.swing._

class ScrollableTextArea extends FlowPanel with ConcurrentDocument {
    val areaEdicion = new NotificationEditPane {
        text = ""
        preferredSize = new Dimension(640, 480)
    }

    val scrollAreaEdicion = new ScrollPane(areaEdicion)

    val debugConsole = new TextArea {
        text = "-- debug console --\n"
        rows = 5
        columns = 20
        editable = false
    }

    val scrollDebugConsole = new ScrollPane(debugConsole)

    val split = new SplitPane {
        orientation = Orientation.Horizontal
        leftComponent = scrollAreaEdicion
        rightComponent = scrollDebugConsole
        oneTouchExpandable = true
    }

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
    }

    contents += split

    override def removeText(pos: Int, count: Int) = {
        areaEdicion.disableFiringEvents
        try {
            val text = areaEdicion.text
            areaEdicion.text = text.substring(0, pos) + text.substring(pos + count)
        } finally {
            areaEdicion.enableFiringEvents
        }
    }

    override def addText(pos: Int, text: String) = {
        areaEdicion.disableFiringEvents
        try {
            val original = areaEdicion.text
            areaEdicion.text = original.substring(0, pos) + original + text.substring(pos)
        } finally {
            areaEdicion.enableFiringEvents
        }
    }
}
