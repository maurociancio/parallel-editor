package ar.noxit.paralleleditor.gui

import remotes.RemoteServerProxy
import scala.swing._
import java.net.Socket
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection
import ar.noxit.paralleleditor.common.messages.{RemoteDocumentListRequest, RemoteAddText, RemoteDeleteText}

class DocumentsAdapter(private val aDoc: ConcurrentDocument, private val menu: HomeMenuBar) extends Documents {
    override def changeDocList(l: List[String]) = menu changeDocList l

    override def byName(title: String) = if (title == "new_document") Some(aDoc) else None
}

object GUI extends SimpleSwingApplication with Loggable {
    var actor: Actor = _
    var connected = false

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        val homeMenuBar = new HomeMenuBar
        menuBar = homeMenuBar

        val connPanel = new ConnectionPanel
        val editArea = new DocumentArea
        val tabs = new TabbedPane {
            pages += new TabbedPane.Page("Doc", editArea)
        }

        val panelGeneral = new BorderPanel()

        panelGeneral.layout(connPanel) = BorderPanel.Position.South
        panelGeneral.layout(tabs) = BorderPanel.Position.Center

        contents = panelGeneral

        listenTo(connPanel)
        listenTo(editArea)
        listenTo(homeMenuBar)

        reactions += {
            case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                val socket = new Socket(host, port.intValue)
                connected = true
                val factory = new GuiActorFactory(new DocumentsAdapter(editArea, homeMenuBar))
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
            case DocumentListRequest() => {
                if (connected) {
                    actor ! RemoteDocumentListRequest()
                }
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
