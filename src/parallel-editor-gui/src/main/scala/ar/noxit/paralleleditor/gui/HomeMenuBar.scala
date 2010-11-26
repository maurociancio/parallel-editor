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

import java.awt.Dimension
import swing._
import ar.noxit.paralleleditor.common.logger.Loggable
import event.{WindowClosing, WindowClosed, ButtonClicked}
import reflect.BeanProperty

class HomeMenuBar extends MenuBar with Loggable {
    @BeanProperty
    var newDocumentDialog: NewDocumentDialog = _

    val newDoc = new MenuItem("Nuevo")
    val newDocFromFile = new MenuItem("Nuevo desde archivo")
    val saveCurrentDocToFile = new MenuItem("Guardar actual")
    val closeCurrent = new MenuItem("Cerrar actual")
    val deleteCurrent = new MenuItem("Borrar actual")
    val exit = new MenuItem("Salir")

    val fileMenu = new Menu("Documento") {
        contents += newDoc
        contents += newDocFromFile
        contents += saveCurrentDocToFile
        contents += new Separator
        contents += closeCurrent
        contents += deleteCurrent
        contents += new Separator
        contents += exit
    }

    val docList = new MenuItem("Listado de documentos")
    val userList = new MenuItem("Listado de usuarios")

    val verMenu = new Menu("Ver") {
        contents += docList
        contents += userList
    }

    var docListFrame: DocumentListFrame = _
    var userListFrame: UserListFrame = _

    listenTo(docList, newDoc, newDocFromFile, saveCurrentDocToFile, closeCurrent, deleteCurrent, userList, exit)

    reactions += {
        // listado de documentos
        case ButtonClicked(`docList`) if docListFrame == null => {
            trace("frame created")

            docListFrame = new DocumentListFrame
            listenTo(docListFrame)

            publish(DocumentListRequest())
        }
        case wc: WindowClosing if wc.source == docListFrame => {
            trace("frame deleted")

            deafTo(docListFrame)
            docListFrame = null
        }

        // listado de usuarios
        case ButtonClicked(`userList`) if userListFrame == null => {
            trace("user list created")

            userListFrame = new UserListFrame
            listenTo(userListFrame)

            publish(UserListRequest())
        }
        case wc: WindowClosing if wc.source == userListFrame => {
            trace("user list closed")

            deafTo(userListFrame)
            userListFrame = null
        }

        case ButtonClicked(`exit`) => {
            trace("exit requested")
            publish(ExitRequested())
        }

        // nuevo documento
        case ButtonClicked(`newDoc`) => {
            newDocumentDialog.askDocumentNameForEmptyFile(this).foreach {publish(_)}
        }
        case ButtonClicked(`newDocFromFile`) => {
            trace("clicked new doc from file")
            newDocumentDialog.askDocumentNameFromFile(this).foreach {publish(_)}
        }

        case ButtonClicked(`saveCurrentDocToFile`) => {
            trace("clicked save doc from file")
            publish(SaveCurrentDocumentRequest())
        }

        // cerrar documento
        case ButtonClicked(`closeCurrent`) => {
            publish(CloseCurrentDocument())
        }

        // borrar documento
        case ButtonClicked(`deleteCurrent`) => {
            publish(DeleteCurrentDocument())
        }

        // suscribir a documento
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

    def changeUserList(usernames: Map[String, List[String]]) {
        trace("changeUserList %s", usernames)

        if (userListFrame != null)
            userListFrame.changeUserList(usernames)
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
        case w: WindowClosed => this.close
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

class UserListFrame extends Frame with Loggable {
    title = "Listado de Usuarios"
    visible = true
    size = new Dimension(320, 400)

    val docs = new ListView(List[String]())
    val scroll = new ScrollPane(docs)

    val border = new BorderPanel
    border.layout(scroll) = BorderPanel.Position.Center

    contents = border

    reactions += {
        case w: WindowClosed => this.close
    }

    def changeUserList(usernames: Map[String, List[String]]) {
        docs.listData = usernames.toList.map {k => k._1 + ": " + k._2.mkString(",")}
        docs.repaint
    }
}
