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
                override def data = areaEdicion.text

                override def replace(offset: Int, length: Int, newText: String) = {
                    val result = data.substring(0, offset) + (if (newText == null) "" else newText) + "" + data.substring(offset + length)
                    areaEdicion.text = result
                }

                val caret = new Caret {
                    def selectionLength = 0
                    def offset = areaEdicion.caret.position
                    def change(offset: Int, selectionLength: Int) = {
                        areaEdicion.caret.position = offset
                    }
                }
            }
            o.executeOn(docData)
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
