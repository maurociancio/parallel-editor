package ar.noxit.paralleleditor.gui

import scala.swing._
import scala.swing.event.ValueChanged

class ScrollableTextArea extends FlowPanel {
    var oldSize = 0

    val areaEdicion = new EditorPane {
        text = "Hola Mundo"
        preferredSize = new Dimension(640, 480)
    }

    val scrollAreaEdicion = new ScrollPane(areaEdicion)

    val debugConsole = new TextArea {
        text = "-- debug console --"
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
            areaEdicion.caret.position_=(areaEdicion.text.length)
            val initPos = areaEdicion.caret.position
            val sizeDiff = calculateDiffSize
            //   if (sizeDiff>0){
            addEntry(evt toString)
            addEntry(
                "Edicion en pos: " + Integer.toString(initPos) +
                        " - Diff tamaño: " + Integer.toString(sizeDiff)
                //   " - Cambio: " + areaEdicion.text.substring(initPos,sizeDiff)
                )
            //   }
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