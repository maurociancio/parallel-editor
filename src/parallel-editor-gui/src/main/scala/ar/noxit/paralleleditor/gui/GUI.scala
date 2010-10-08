package ar.noxit.paralleleditor.gui

import remotes.RemoteServerProxy
import scala.swing._
import java.net.Socket
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection
import ar.noxit.paralleleditor.common.messages._

class DocumentsAdapter(private val tabs: TabbedPane,
                       private val menu: HomeMenuBar,
                       private val gui: Reactor) extends Documents {
    override def changeDocList(l: List[String]) = menu changeDocList l

    override def byName(title: String) = {
        val page = tabs.pages.find {page => page.title == title}
        page.map {p => p.asInstanceOf[DocumentPage].docArea}
    }

    def createDocument(title: String, content: String) {
        val doc = new DocumentArea(content)
        gui.listenTo(doc)
        tabs.pages += new DocumentPage(title, doc)
    }
}

class DocumentPage(private val tabTitle: String, val docArea: DocumentArea) extends TabbedPane.Page(null, tabTitle, docArea, null)

object GUI extends SimpleSwingApplication with Loggable {
    var actor: Actor = _
    var connected = false

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        val homeMenuBar = new HomeMenuBar
        menuBar = homeMenuBar

        val connPanel = new ConnectionPanel
        val tabs = new TabbedPane

        val panelGeneral = new BorderPanel()

        panelGeneral.layout(connPanel) = BorderPanel.Position.South
        panelGeneral.layout(tabs) = BorderPanel.Position.Center

        contents = panelGeneral

        listenTo(connPanel)
        listenTo(homeMenuBar)

        val documents = new DocumentsAdapter(tabs, homeMenuBar, this)

        reactions += {
            case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                connected = true
                val socket = new Socket(host, port.intValue)
                val factory = new GuiActorFactory(documents)
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
            case SubscribeToDocument(title) => {
                if (connected) {
                    actor ! RemoteSubscribeRequest(title)
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
