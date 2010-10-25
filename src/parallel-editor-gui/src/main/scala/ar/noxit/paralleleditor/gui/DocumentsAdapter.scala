package ar.noxit.paralleleditor.gui

import swing.TabbedPane.Page
import swing.{Reactor, MainFrame, Dialog, TabbedPane}
import ar.noxit.paralleleditor.client.Documents

class DocumentsAdapter(private val tabs: TabbedPane,
                       private val menu: HomeMenuBar,
                       private val gui: Reactor) extends Documents {
    override def changeDocList(l: List[String]) = menu changeDocList l

    override def byName(title: String) = {
        val page = tabs.pages.find {page => page.title == title}
        page.map {p => p.content.asInstanceOf[DocumentArea]}
    }

    override def createDocument(title: String, content: String) {
        val doc = new DocumentArea(title, content)
        gui.listenTo(doc)
        tabs.pages += new Page(title, doc)
    }

    override def usernameTaken = {
        SwingUtil.invokeLater {
            Dialog.showMessage(parent = menu, message = "Nombre de usuario ya existe, intente con otro")
        }
    }
}
