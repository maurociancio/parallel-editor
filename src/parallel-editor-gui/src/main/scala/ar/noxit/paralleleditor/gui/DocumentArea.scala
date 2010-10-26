package ar.noxit.paralleleditor.gui

import scala.swing._
import ar.noxit.paralleleditor.common.{BasicXFormStrategy, EditOperationJupiterSynchronizer}
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation._
import reflect.BeanProperty

class DocumentArea(private val docTitle: String, private val initialContent: String) extends SplitPane {
    dividerLocation = 150

    // TODO
    @BeanProperty
    var sync = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)

    private val areaEdicion = new NotificationEditPane {
        text = initialContent
    }

    private val scrollAreaEdicion = new ScrollPane(areaEdicion)
    scrollAreaEdicion preferredSize = new Dimension(320, 240)

    private val debugConsole = new TextArea {
        text = "-- debug console --\n"
        editable = false
    }
    private val scrollDebugConsole = new ScrollPane(debugConsole)

    orientation = Orientation.Horizontal
    leftComponent = scrollAreaEdicion
    rightComponent = scrollDebugConsole
    oneTouchExpandable = true

    listenTo(areaEdicion)

    reactions += {
        case WrappedEvent(e) => {
            addEntry("event received %s".format(e))

            val op = e match {
                case InsertionEvent(pos, text) => generateOp(new AddTextOperation(text, pos))
                case DeletionEvent(pos, count) => {
                    //generar ops de borrado de a 1
                    (1 to count).foreach {Int => generateOp(new DeleteTextOperation(pos, 1))}
                }
            }
        }
    }

    def processRemoteOperation(m: Message[EditOperation]) {
        SwingUtil.invokeLater {
            sync.receive(m, {op => processOperation(op)})
        }
    }

    private def generateOp(op: EditOperation) {
        sync.generate(op, {
            msg =>
                val docOp = new DocumentOperation(docTitle, msg)
                publish(OperationEvent(docOp))
        })
    }

    private def addEntry(msg: String) {
        debugConsole append (msg + '\n')
        debugConsole.caret.position = debugConsole.text.size
    }

    private def processOperation(o: EditOperation) = {
        doInGuard({
            val docData = new DocumentData {
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
