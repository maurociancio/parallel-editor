package ar.noxit.paralleleditor.gui

import remotes.RemoteServerProxy
import scala.swing._
import java.net.Socket
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages.{RemoteAddText, RemoteDeleteText}
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection

trait ConcurrentDocument {
    def addText(pos: Int, text: String)

    def removeText(pos: Int, count: Int)

    def initialContent(content: String)
}

object GUI extends SimpleSwingApplication with Loggable {
    var actor: Actor = _
    var connected = false

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new HomeMenuBar

        val connPanel = new ConnectionPanel
        val editArea = new DocumentArea

        val panelGeneral = new BorderPanel()

        panelGeneral.layout(connPanel) = BorderPanel.Position.South
        panelGeneral.layout(editArea) = BorderPanel.Position.Center

        contents = panelGeneral

        listenTo(connPanel)
        listenTo(editArea)

        reactions += {
            case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                val socket = new Socket(host, port.intValue)
                connected = true
                val factory = new GuiActorFactory(editArea)
                new RemoteServerProxy(new SocketNetworkConnection(socket), factory)

                actor = factory.guiActor
                actor ! Login(connPanel user)
            }

            case DisconnectionRequest() => {
                actor ! Logout()
            }

            case InsertionEvent(pos, text) => {
                trace("Insertion required " + text + pos)
                if (connected)
                    actor ! RemoteAddText(text, pos)
            }
            case DeletionEvent(pos, count) => {
                trace("Deletion required" + pos + "," + count)
                if (connected)
                    actor ! RemoteDeleteText(pos, count)
            }

        }
    }

    override def shutdown() {
        //TODO ver bien como terminar de desloguearse y cerrar sockets
        //este metodo se llama antes de cerrar la ventana
        if (connected)
            actor ! Logout()
    }
}
