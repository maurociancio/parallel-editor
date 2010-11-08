package ar.noxit.paralleleditor.gui

import ar.noxit.paralleleditor.gui.FileHelper._
import swing.{Component, FileChooser}

trait FileWriterDialog {
    def askAndWriteFile(over: Component, content: => String)
}

class DefaultFileWriterDialog extends FileWriterDialog {
    def askAndWriteFile(over: Component, content: => String) = {
        val chooser = new FileChooser()

        chooser.showSaveDialog(over) match {
            case FileChooser.Result.Approve => {
                val file = chooser.selectedFile
                file.write(content)
            }
            case _ => {}
        }
    }
}
