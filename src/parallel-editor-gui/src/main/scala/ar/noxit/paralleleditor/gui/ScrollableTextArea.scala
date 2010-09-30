package ar.noxit.paralleleditor.gui

import scala.swing._
import scala.swing.event.ValueChanged

class ScrollableTextArea extends FlowPanel {
    val areaEdicion = new NotificationEditPane {
        text = "Hola Mundo"
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
}
