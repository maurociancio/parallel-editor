package ar.noxit.paralleleditor.gui

import scala.swing._
import scala.actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.converter._
import reflect.BeanProperty
import ar.noxit.paralleleditor.client.{Logout, SynchronizationSessionFactory}

class GUI extends SimpleSwingApplication with Loggable {
    var actor: Actor = _
    var connected = false

    @BeanProperty
    var remoteDocOpConverter: RemoteDocumentOperationConverter = _

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        val homeMenuBar = new HomeMenuBar
        menuBar = homeMenuBar

        val connPanel = new ConnectionPanel
        val tabs = new TabbedPane {
            preferredSize = new Dimension(300, 200)
        }

        val panelGeneral = new BorderPanel

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
                actor = SynchronizationSessionFactory.getSyncServerSession(host, port, documents)
                actor ! RemoteLoginRequest(connPanel user)
            }

            case DisconnectionRequest() => {
                if (connected)
                    actor ! Logout()
            }

            case CloseCurrentDocument() => {
                if (connected) {
                    currentDocument.foreach {
                        selection =>
                            actor ! RemoteUnsubscribeRequest(selection.docTitle)
                            tabs.pages.remove(selection.index)
                    }
                }
            }
            case DeleteCurrentDocument() => {
                if (connected) {
                    currentDocument.foreach {
                        selection =>
                            actor ! RemoteDeleteDocumentRequest(selection.docTitle)
                    }
                }
            }

            case OperationEvent(docOp) => {
                if (connected) {
                    actor ! remoteDocOpConverter.convert(docOp)
                }
            }

            // listado de documentos
            case DocumentListRequest() => {
                if (connected) {
                    actor ! RemoteDocumentListRequest()
                }
            }
            // listado de usuarios
            case UserListRequest() => {
                if (connected) {
                    actor ! RemoteUserListRequest()
                }
            }

            case NewDocumentRequest(docTitle, initialContent) => {
                if (connected) {
                    actor ! RemoteNewDocumentRequest(docTitle, initialContent)
                }
            }
            case SubscribeToDocument(title) => {
                if (connected) {
                    actor ! RemoteSubscribeRequest(title)
                }
            }
        }

        private def currentDocument = {
            val selected = tabs.selection.index
            if (selected != -1)
                Some(SelectedDocument(selected, tabs.pages(selected).title))
            else
                None
        }
    }

    override def shutdown() {
        //TODO ver bien como terminar de desloguearse y cerrar sockets
        //este metodo se llama antes de cerrar la ventana
        if (connected)
            actor ! Logout()
    }
}

case class SelectedDocument(val index: Int, val docTitle: String)
