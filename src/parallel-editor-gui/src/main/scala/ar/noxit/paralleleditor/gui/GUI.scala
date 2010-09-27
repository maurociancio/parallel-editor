package ar.noxit.paralleleditor.gui

import remotes.RemoteServerProxy
import scala.swing._
import java.net.Socket
import ar.noxit.paralleleditor.common.logger.Loggable

object GUI extends SimpleSwingApplication with Loggable {
    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new HomeMenuBar

        val connPanel = new ConnectionPanel
        contents = new BoxPanel(Orientation.Vertical) {
            contents += new ScrollableTextArea
            contents += connPanel
        }

        listenTo(connPanel)
        reactions += {
            case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                val socket = new Socket(host, port.intValue)
                val factory = new GuiActorFactory
                new RemoteServerProxy(socket, factory)

                val actor = factory.guiActor
                actor ! ("login", "pepe")
            }
        }
    }
}
