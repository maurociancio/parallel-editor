/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
