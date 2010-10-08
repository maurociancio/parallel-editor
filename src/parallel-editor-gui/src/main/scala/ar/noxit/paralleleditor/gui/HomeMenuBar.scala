package ar.noxit.paralleleditor.gui

import java.awt.Dimension
import swing._
import event.{WindowDeactivated, WindowClosed, ButtonClicked}
import ar.noxit.paralleleditor.common.logger.Loggable

class HomeMenuBar extends MenuBar with Loggable {
    val fileMenu = new Menu("Archivo") {
        contents += new MenuItem("Abrir")
        contents += new MenuItem("Guardar")
        contents += new MenuItem("Guardar Como")
        contents += new MenuItem("Salir")
    }

    val editMenu = new Menu("Edicion") {
        contents += new MenuItem("Copiar")
        contents += new MenuItem("Cortar")
        contents += new MenuItem("Pegar")
        contents += new MenuItem("Buscar...")
    }

    val docList = new MenuItem("Listado de documentos")
    val verMenu = new Menu("Ver") {
        contents += docList
    }

    var docListFrame: DocumentListFrame = _

    listenTo(docList)
    reactions += {
        case c: ButtonClicked if c.source == docList && docListFrame == null => {
            trace("frame created")

            docListFrame = new DocumentListFrame
            listenTo(docListFrame)

            publish(DocumentListRequest())
        }
        case w: WindowClosed if w.source == docListFrame => {
            trace("frame deleted")

            deafTo(docListFrame)
            docListFrame = null
        }
        case WrappedEvent(e: SubscribeToDocument) => {
            trace("subscribe to document")
            publish(e)
        }
    }

    this.contents += fileMenu
    this.contents += editMenu
    this.contents += verMenu

    def changeDocList(l: List[String]) {
        trace("changeDocList %s", l)

        docListFrame.changeDocList(l)
    }
}

class DocumentListFrame extends Frame with Loggable {
    title = "Listado de Documentos"
    visible = true
    size = new Dimension(320, 400)

    val docs = new ListView(List[String]())
    val scroll = new ScrollPane(docs)

    val border = new BorderPanel
    border.layout(scroll) = BorderPanel.Position.Center

    val editar = new Button("editar")
    border.layout(editar) = BorderPanel.Position.South

    contents = border

    listenTo(editar)

    reactions += {
        case w: WindowDeactivated => {
            this.dispose
        }
        case ButtonClicked(source) if source == editar && docs.selection.indices.size >= 1 => {
            val firstSelected = docs.selection.items.head
            publish(WrappedEvent(SubscribeToDocument(firstSelected)))
        }
    }

    def changeDocList(l: List[String]) {
        trace("with Loggable")
        docs.listData = l
        docs.repaint
    }
}
