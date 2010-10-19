package ar.noxit.paralleleditor.gui

import scala.swing._
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.converter._
import reflect.BeanProperty

trait ClientActorFactory {
    def newActor(host: String, port: Int, docs: Documents): Actor
}

class GUI extends SimpleSwingApplication with Loggable {
    var actor: Actor = _
    var connected = false

    @BeanProperty
    var remoteDocOpConverter: RemoteDocumentOperationConverter = _

    @BeanProperty
    val clientActorFactory: ClientActorFactory = null

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
                actor = clientActorFactory.newActor(host, port.intValue, documents)
                actor ! RemoteLoginRequest(connPanel user)
            }

            case DisconnectionRequest() => {
                if (connected)
                    actor ! Logout()
            }
            case OperationEvent(docOp) => {
                if (connected) {
                    actor ! remoteDocOpConverter.convert(docOp)
                }
            }

            case DocumentListRequest() => {
                if (connected) {
                    actor ! RemoteDocumentListRequest()
                }
            }
            case NewDocumentRequest(docTitle) => {
                if (connected) {
                    actor ! RemoteNewDocumentRequest(docTitle)
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
