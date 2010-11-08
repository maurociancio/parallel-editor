package ar.noxit.paralleleditor.gui

import javax.swing.filechooser.FileNameExtensionFilter
import io.Source
import swing.{Component, FileChooser, Dialog}

trait NewDocumentDialog {
    def askDocumentNameForEmptyFile(over: Component): Option[NewDocumentRequest]

    def askDocumentNameFromFile(over: Component): Option[NewDocumentRequest]
}

class DefaultNewDocumentDialog extends NewDocumentDialog {
    def askDocumentNameForEmptyFile(over: Component) =
        askAndPublishDocumentName {""}

    def askDocumentNameFromFile(over: Component) = {
        val chooser = new FileChooser()
        chooser.fileFilter = new FileNameExtensionFilter("Archivos de texto", "txt", "tex", "html", "htm")

        chooser.showOpenDialog(over) match {
            case FileChooser.Result.Approve => {
                val selectedFile = chooser.selectedFile
                askAndPublishDocumentName({
                    Source.fromFile(selectedFile).getLines.mkString("\n")
                }, selectedFile.getName)
            }
            case _ => None
        }
    }

    protected def askAndPublishDocumentName(initialContent: => String = {""}, defaultName: String = "Nuevo Documento") = {
        val input = Dialog.showInput(message = "Ingrese el nombre del nuevo documento", initial = defaultName)
        input.map {newDoc => NewDocumentRequest(newDoc, initialContent)}
    }
}
