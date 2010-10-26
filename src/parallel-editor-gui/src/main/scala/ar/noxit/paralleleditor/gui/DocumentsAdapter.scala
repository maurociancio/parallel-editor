package ar.noxit.paralleleditor.gui

import swing.TabbedPane.Page
import swing.{Reactor, Dialog, TabbedPane}
import ar.noxit.paralleleditor.client.Documents
import ar.noxit.paralleleditor.{UsernameTaken, DocumentSubscription, DocumentListUpdate, ProcessOperation}

class DocumentsAdapter(private val tabs: TabbedPane,
                       private val menu: HomeMenuBar,
                       private val gui: Reactor) extends Documents {
    override def process(msg: Any) = {
        msg match {
            case ProcessOperation(title, msg) => {
                val page = tabs.pages.find {page => page.title == title}
                page.foreach {p => p.content.asInstanceOf[DocumentArea].processRemoteOperation(msg)}
            }

            case DocumentListUpdate(docs) =>
                menu changeDocList docs

            case DocumentSubscription(title, initialContent) => {
                val doc = new DocumentArea(title, initialContent)
                gui.listenTo(doc)
                tabs.pages += new Page(title, doc)
            }

            case UsernameTaken() => {
                SwingUtil.invokeLater {
                    Dialog.showMessage(parent = menu, message = "Nombre de usuario ya existe, intente con otro")
                }
            }
        }
    }
}
