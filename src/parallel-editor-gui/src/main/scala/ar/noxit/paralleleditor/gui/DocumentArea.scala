package ar.noxit.paralleleditor.gui

import scala.swing._
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation._
import reflect.BeanProperty

trait Synchronizer {
    def generate(op: EditOperation, send: Message[EditOperation] => Unit)

    def receive(message: Message[EditOperation], apply: EditOperation => Unit)
}

class DocumentArea(private val docTitle: String, private val initialContent: String) extends ScrollPane {

    @BeanProperty
    var sync: Synchronizer = _

    private val areaEdicion = new NotificationEditPane {
        text = initialContent
    }

    private val scrollAreaEdicion = new ScrollPane(areaEdicion)

    scrollAreaEdicion preferredSize = new Dimension(320, 240)

    contents = scrollAreaEdicion

    listenTo(areaEdicion)

    reactions += {
        case WrappedEvent(e) => {
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

    private def processOperation(o: EditOperation) = {
        doInGuard({
            val docData = new DocumentData {
                var data = areaEdicion.text

                // TODO implementar en GUI
                val caret = new Caret {
                    val selectionLength = 0
                    val offset = 0

                    def change(offset: Int, selectionLength: Int) = {}
                }
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

    def text = areaEdicion text
}
