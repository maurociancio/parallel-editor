package ar.noxit.paralleleditor.gui

import java.awt.Dimension
import swing._
import event.{WindowDeactivated, WindowClosed, ButtonClicked}
import ar.noxit.paralleleditor.common.logger.Loggable
import javax.swing.filechooser.FileNameExtensionFilter
import io.Source

class HomeMenuBar extends MenuBar with Loggable {
    val newDoc = new MenuItem("Nuevo")
    val newDocFromFile = new MenuItem("Nuevo desde archivo")
    val closeCurrent = new MenuItem("Cerrar actual")
    val deleteCurrent = new MenuItem("Borrar actual")

    val fileMenu = new Menu("Documento") {
        contents += newDoc
        contents += newDocFromFile
        contents += closeCurrent
        contents += deleteCurrent
    }

    val docList = new MenuItem("Listado de documentos")
    val verMenu = new Menu("Ver") {
        contents += docList
    }

    var docListFrame: DocumentListFrame = _

    listenTo(docList, newDoc, newDocFromFile, closeCurrent, deleteCurrent)

    reactions += {
        case ButtonClicked(`docList`) if docListFrame == null => {
            trace("frame created")

            docListFrame = new DocumentListFrame
            listenTo(docListFrame)

            publish(DocumentListRequest())
        }
        case ButtonClicked(`newDoc`) => {
            askAndPublishDocumentName {""}
        }
        case ButtonClicked(`newDocFromFile`) => {
            trace("clicked new doc from file")

            val chooser = new FileChooser()
            chooser.fileFilter = new FileNameExtensionFilter("Archivos de texto", "txt", "tex", "html", "htm")
            chooser.showOpenDialog(this) match {
                case FileChooser.Result.Approve => {
                    val selectedFile = chooser.selectedFile
                    askAndPublishDocumentName({
                        Source.fromFile(selectedFile).getLines.mkString("\n")
                    }, selectedFile.getName)
                }
                case _ => {}
            }
        }
        case ButtonClicked(`closeCurrent`) => {
            publish(CloseCurrentDocument())
        }
        case ButtonClicked(`deleteCurrent`) => {
            publish(DeleteCurrentDocument())
        }
        case wc: WindowClosed if wc.source == docListFrame => {
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
    this.contents += verMenu

    def changeDocList(l: List[String]) {
        trace("changeDocList %s", l)

        if (docListFrame != null)
            docListFrame.changeDocList(l)
    }

    def askAndPublishDocumentName(initialContent: => String = {""}, defaultName: String = "Nuevo Documento") = {
        val input = Dialog.showInput(message = "Ingrese el nombre del nuevo documento", initial = defaultName)
        input.foreach(newDoc => publish(NewDocumentRequest(newDoc, initialContent)))
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
        trace("change doc list")
        docs.listData = l
        docs.repaint
    }
}
