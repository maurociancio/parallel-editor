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

    @BeanProperty
    var fileWriterDialog: FileWriterDialog = _

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
            // conectar
            case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                connected = true
                actor = SynchronizationSessionFactory.getSyncServerSession(host, port, documents)
                actor ! RemoteLoginRequest(connPanel user)
            }

            case SaveCurrentDocumentRequest() =>
                currentDocument.foreach {doc => fileWriterDialog.askAndWriteFile(menuBar, {documentText(doc.index)})}

            case ExitRequested() => quit()

            case message: Any if connected => {
                message match {
                // nuevo documento
                    case NewDocumentRequest(docTitle, initialContent) =>
                        actor ! RemoteNewDocumentRequest(docTitle, initialContent)

                    // operaciones
                    case OperationEvent(docOp) =>
                        actor ! remoteDocOpConverter.convert(docOp)

                    // suscribirse
                    case SubscribeToDocument(title) =>
                        actor ! RemoteSubscribeRequest(title)

                    // cerrar documento actual
                    case CloseCurrentDocument() =>
                        currentDocument.foreach {
                            selection =>
                                actor ! RemoteUnsubscribeRequest(selection.docTitle)
                                tabs.pages.remove(selection.index)
                        }

                    // borrar documento actual
                    case DeleteCurrentDocument() =>
                        currentDocument.foreach {
                            selection => actor ! RemoteDeleteDocumentRequest(selection.docTitle)
                        }

                    // listado de documentos
                    case DocumentListRequest() =>
                        actor ! RemoteDocumentListRequest()

                    // listado de usuarios
                    case UserListRequest() =>
                        actor ! RemoteUserListRequest()

                    // desconectar
                    case DisconnectionRequest() =>
                        actor ! Logout()

                    // otros
                    case other: Any => {}
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

        private def documentText(index: Int) =
            tabs.pages(index).content.asInstanceOf[DocumentArea].text
    }

    override def shutdown() {
        //TODO ver bien como terminar de desloguearse y cerrar sockets
        //este metodo se llama antes de cerrar la ventana
        if (connected)
            actor ! Logout()
    }
}

case class SelectedDocument(val index: Int, val docTitle: String)
