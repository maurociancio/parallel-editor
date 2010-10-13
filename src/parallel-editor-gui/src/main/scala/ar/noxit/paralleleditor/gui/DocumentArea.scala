package ar.noxit.paralleleditor.gui

import scala.swing._
import ar.noxit.paralleleditor.common.{BasicXFormStrategy, EditOperationJupiterSynchronizer}
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation._

class DocumentArea(private val docTitle: String, private val initialContent: String) extends SplitPane with ConcurrentDocument {

    val sync = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)


    val areaEdicion = new NotificationEditPane(docTitle) {
        text = initialContent
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

            println("LOCAL OPERATION PERFORMED")
            var op:EditOperation = NullOperation
            e match {
                case InsertionEvent(title, pos, text) => op = new AddTextOperation(text,pos)
                case DeletionEvent(title, pos, count) => op = new DeleteTextOperation(pos,count)
                case _ => {}
            }

            sync.generateMsg(op,{op => } )
            publish(e)
        }
    }

    def addEntry(msg: String) {
        debugConsole append (msg + '\n')
        debugConsole.caret.position = debugConsole.text.size
    }

    def processRemoteOperation(o:EditOperation){
        println("REMOTE OPERATION RECEIVED")
        sync.receiveMsg(new Message(o,0,0),{op => })
        processOperation(o)
    }

    def processOperation(o: EditOperation) = {
        doInGuard({
            val docData = new DocumentData{
                var data = areaEdicion.text
            }
            o.executeOn(docData)
            areaEdicion.text = docData.data
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
