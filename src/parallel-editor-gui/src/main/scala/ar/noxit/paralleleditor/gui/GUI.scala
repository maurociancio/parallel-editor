package ar.noxit.paralleleditor.gui

import remotes.RemoteServerProxy
import scala.swing._
import java.net.Socket
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable

object GUI extends SimpleSwingApplication with Loggable {

    var actor:Actor = _
    var connected = false

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new HomeMenuBar

        val connPanel = new ConnectionPanel
        val editArea =   new ScrollableTextArea

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
                val factory = new GuiActorFactory
                new RemoteServerProxy(socket, factory)

                actor = factory.guiActor
                actor ! ("login", "pepe")
            }

            case InsertionEvent(pos,text) => {
                trace("Insertion required " +text+pos)
                if (connected)
                actor ! ("insertion",pos,text)

            }
            case DeletionEvent(pos,count) => {
                trace("Deletion required"+pos+","+count)
                if (connected)
                actor ! ("deletion",pos,count)
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
