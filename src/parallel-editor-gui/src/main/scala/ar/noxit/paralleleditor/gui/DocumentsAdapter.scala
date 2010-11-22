package ar.noxit.paralleleditor.gui

import swing.TabbedPane.Page
import swing.{Reactor, Dialog, TabbedPane}
import ar.noxit.paralleleditor.common.{BasicXFormStrategy, EditOperationJupiterSynchronizer}
import sync.SynchronizerAdapter
import ar.noxit.paralleleditor.client._

class DocumentsAdapter(private val tabs: TabbedPane,
                       private val menu: HomeMenuBar,
                       private val gui: Reactor,
                       private val logger: GUILogger) extends Documents {

    override def process(msg: CommandFromKernel) = {
        msg match {
            case ProcessOperation(title, msg) => {
                val page = tabs.pages.find {page => page.title == title}
                page.foreach {p => p.content.asInstanceOf[DocumentArea].processRemoteOperation(msg)}
            }

            case DocumentListUpdate(docs) =>
                menu changeDocList docs

            case UserListUpdate(usernames) =>
                menu changeUserList usernames

            case DocumentSubscription(title, initialContent) => {
                val doc = newDocumentArea(title, initialContent)
                gui.listenTo(doc)
                tabs.pages += new Page(title, doc)
            }

            case UsernameTaken() => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "Nombre de usuario ya existe, intente con otro")
                }
            }
            case DocumentTitleTaken(offenderTitle) => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "Nombre de documento '%s' ya tomado".format(offenderTitle))
                }
            }
            case DocumentSubscriptionAlreadyExists(offenderTitle) => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "Ya estas suscripto al documento '%s'".format(offenderTitle))
                }
            }
            case DocumentSubscriptionNotExists(offenderTitle) => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "No estas suscripto al documento '%s'".format(offenderTitle))
                }
            }
            case DocumentInUse(docTitle) => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "Documento en uso '%s'".format(docTitle))
                }
            }
            case DocumentDeleted(docTitle) => {
                SwingUtil.invokeLater {
                    (0 until tabs.pages.size).zip(tabs.pages).find {tuple => tuple._2.title == docTitle}.foreach {
                        tuple => tabs.pages.remove(tuple._1)
                    }

                    Dialog.showMessage(parent = menu, message = "Document borrado %s".format(docTitle))
                }
            }
            case DocumentDeletionTitleNotExists(docTitle) => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "El documento '%s' no existe".format(docTitle))
                }
            }

            case NewUserLoggedIn(username) => {
                SwingUtil.invokeLater {
                    logEvent("El usuario '%s' se ha logueado".format(username))
                }
            }
            case UserLoggedOut(username) => {
                SwingUtil.invokeLater {
                    logEvent("El usuario '%s' se ha deslogueado".format(username))
                }
            }

            case NewSubscriberToDocument(username, docTitle) => {
                SwingUtil.invokeLater {
                    logEvent("Nuevo usuario '%s' en documento '%s'".format(username, docTitle))
                }
            }

            case SubscriberLeftDocument(username, docTitle) => {
                SwingUtil.invokeLater {
                    logEvent("El usuario '%s' dejó el documento '%s'.".format(username, docTitle))
                }
            }

            case DocumentTitleNotExists(offenderTitle) => {
                SwingUtil.invokeLater {
                    logEvent("No existe titulo '%s'".format(offenderTitle))
                }
            }

            case ChatMessage(username, message) => {
                SwingUtil.invokeLater {
                    logEvent("%s dijo: %s".format(username, message))
                }
            }

            case SubscriptionCancelled(docTitle) => {
            }

            case LoginOk() => {
            }
        }
    }

    protected def newDocumentArea(title: String, initialContent: String) = {
        val doc = new DocumentArea(title, initialContent)
        doc.sync = new SynchronizerAdapter(new EditOperationJupiterSynchronizer(new BasicXFormStrategy))
        doc
    }

    private def logEvent(msg: String) = logger trace (msg)
}
