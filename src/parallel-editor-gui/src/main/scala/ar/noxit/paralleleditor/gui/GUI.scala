package ar.noxit.paralleleditor.gui

import remotes.RemoteServerProxy
import scala.swing._
import java.net.Socket
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages.{AddText, DeleteText}

trait ConcurrentDocument {
    def addText(pos: Int, text: String)
    def removeText(pos: Int, count: Int)
}

object GUI extends SimpleSwingApplication with Loggable {
    var actor: Actor = _
    var connected = false

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new HomeMenuBar

        val connPanel = new ConnectionPanel
        val editArea = new ScrollableTextArea

        contents = new BoxPanel(Orientation.Vertical) {
            contents += editArea
            contents += connPanel
        }

        listenTo(connPanel)
        listenTo(editArea)

        reactions += {
            case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                val socket = new Socket(host, port.intValue)
                connected = true
                val factory = new GuiActorFactory(editArea)
                new RemoteServerProxy(socket, factory)

                actor = factory.guiActor
                actor ! ("login", "pepe")
            }

            case InsertionEvent(pos, text) => {
                trace("Insertion required " + text + pos)
                if (connected)
                    actor ! AddText(text, pos)

            }
            case DeletionEvent(pos, count) => {
                trace("Deletion required" + pos + "," + count)
                if (connected)
                    actor ! DeleteText(pos, count)
            }

        }
    }

    override def shutdown() {
        //TODO ver bien como terminar de desloguearse y cerrar sockets
        //este metodo se llama antes de cerrar la ventana
        if (connected)
            actor ! ("logout")
    }
}
