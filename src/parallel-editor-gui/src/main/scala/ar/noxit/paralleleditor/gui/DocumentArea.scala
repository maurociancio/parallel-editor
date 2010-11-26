/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
            e match {
                //generar ops de a 1
                case InsertionEvent(pos, text) =>
                    (0 until text.size) foreach {index => generateOp(new AddTextOperation(text.substring(index, index + 1), pos + index))}
                case DeletionEvent(pos, count) =>
                    (1 to count).foreach {Int => generateOp(new DeleteTextOperation(pos, 1))}
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
