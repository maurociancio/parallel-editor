package ar.noxit.paralleleditor.gui

import scala.swing._

class DocumentArea extends SplitPane with ConcurrentDocument {
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
        case WrappedEvent(e) => {
            addEntry("event received %s".format(e))
            publish(e)
        }
    }

    def addEntry(msg: String) {
        debugConsole append (msg + '\n')
        debugConsole.caret.position = debugConsole.text.size
    }

    override def removeText(pos: Int, count: Int) = {
        doInGuard({
            val text = areaEdicion.text
            areaEdicion.text = text.substring(0, pos) + text.substring(pos + count)
        })
    }

    override def addText(pos: Int, text: String) = {
        doInGuard({
            val original = areaEdicion.text
            areaEdicion.text = original.substring(0, pos) + text + original.substring(pos)
        })
    }

    override def initialContent(content: String) = {
        doInGuard({
            areaEdicion.text = content
        })
    }

    private def doInGuard(closure: => Unit) = {
        try {
            areaEdicion.disableFiringEvents
            closure
            areaEdicion.repaint
        }
        finally
            areaEdicion.enableFiringEvents
    }
}
