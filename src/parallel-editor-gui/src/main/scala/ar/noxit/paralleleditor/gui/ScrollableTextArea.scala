package ar.noxit.paralleleditor.gui

import scala.swing._
import scala.swing.event.ValueChanged

class ScrollableTextArea extends FlowPanel {
    val areaEdicion = new EditorPane {
        text = "Hola Mundo"
        preferredSize = new Dimension(640, 480)
    }

    var oldSize = areaEdicion.text.size

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
        case evt: ValueChanged => {
            val initPos = areaEdicion.caret.position
            val sizeDiff = calculateDiffSize
            addEntry(
                "Edicion en pos: " + Integer.toString(initPos) +
                        " - Diff tamaño: " + Integer.toString(sizeDiff)
                )
        }
    }

    def calculateDiffSize = {
        val diff = areaEdicion.text.length - oldSize
        oldSize = areaEdicion.text.length
        diff
    }

    def addEntry(msg: String) {
        debugConsole append (msg + '\n')
    }

    contents += split
}
