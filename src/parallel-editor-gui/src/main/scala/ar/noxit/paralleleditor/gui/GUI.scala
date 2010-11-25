package ar.noxit.paralleleditor.gui

import scala.swing._
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.converter._
import reflect.BeanProperty
import ar.noxit.paralleleditor.client.{Session, Logout, SessionFactory}

class GUI extends SimpleSwingApplication with Loggable {
    var currentSession: Session = _
    var connected = false

    @BeanProperty
    var remoteDocOpConverter: RemoteDocumentOperationConverter = _
    @BeanProperty
    var fileWriterDialog: FileWriterDialog = _
    @BeanProperty
    var homeMenuBar: HomeMenuBar = _

    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = homeMenuBar

        private val connPanel = new ConnectionPanel

        private val tabs = new TabbedPane {
            preferredSize = new Dimension(300, 200)
        }

        private val debugConsole = new TextArea with GUILogger {
            text = "-- debug console --\n"
            editable = false
            def trace(msg: String) {
                append(msg + '\n')
                caret.position = text.size
            }
        }
        private val scrollDebugConsole = new ScrollPane(debugConsole)

        val split = new SplitPane {
            orientation = Orientation.Horizontal
            leftComponent = tabs
            rightComponent = scrollDebugConsole
            oneTouchExpandable = true
        }

        val panelGeneral = new BorderPanel

        panelGeneral.layout(connPanel) = BorderPanel.Position.South
        panelGeneral.layout(split) = BorderPanel.Position.Center

        contents = panelGeneral

        listenTo(connPanel)
        listenTo(homeMenuBar)

        val documents = new DocumentsAdapter(tabs, homeMenuBar, this, debugConsole)

        reactions += {
            // conectar
          case ConnectionRequest(host, port) => {
                trace("Connecting to %s %s", host, port)

                connected = true
                currentSession = SessionFactory.newSession(host, port, documents)
                currentSession ! RemoteLoginRequest(connPanel user)
            }

            case SaveCurrentDocumentRequest() =>
                currentDocument.foreach {doc => fileWriterDialog.askAndWriteFile(menuBar, {documentText(doc.index)})}

            case ExitRequested() => quit()

            case message: Any if connected => {
                message match {
                // nuevo documento
                    case NewDocumentRequest(docTitle, initialContent) =>
                        currentSession ! RemoteNewDocumentRequest(docTitle, initialContent)

                    // operaciones
                    case OperationEvent(docOp) =>
                        currentSession ! remoteDocOpConverter.convert(docOp)

                    // suscribirse
                    case SubscribeToDocument(title) =>
                        currentSession ! RemoteSubscribeRequest(title)

                    // cerrar documento actual
                    case CloseCurrentDocument() =>
                        currentDocument.foreach {
                            selection =>
                                closeDocument(selection)
                        }

                    // borrar documento actual
                    case DeleteCurrentDocument() =>
                        currentDocument.foreach {
                            selection => currentSession ! RemoteDeleteDocumentRequest(selection.docTitle)
                        }

                    // listado de documentos
                    case DocumentListRequest() =>
                        currentSession ! RemoteDocumentListRequest()

                    // listado de usuarios
                    case UserListRequest() =>
                        currentSession ! RemoteUserListRequest()

                    // desconectar
                    case DisconnectionRequest() => {
                      var doc = currentDocument
                      while (currentDocument.isDefined)
                            currentDocument.foreach(selection => closeDocument(selection))
                      currentSession ! Logout()
                      currentSession close
                    }

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

        private def closeDocument(selection: SelectedDocument): TabbedPane.Page = {
          currentSession ! RemoteUnsubscribeRequest(selection.docTitle)
          tabs.pages.remove(selection.index)
        }


    }



    override def shutdown() {
        //TODO ver bien como terminar de desloguearse y cerrar sockets
        //este metodo se llama antes de cerrar la ventana
        if (connected)
            currentSession ! Logout()
    }
}

trait GUILogger {
    def trace(msg: String)
}

case class SelectedDocument(val index: Int, val docTitle: String)
