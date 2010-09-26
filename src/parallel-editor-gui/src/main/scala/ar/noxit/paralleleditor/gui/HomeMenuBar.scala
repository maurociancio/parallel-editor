package ar.noxit.paralleleditor.gui

import scala.swing.{MenuBar, Menu, MenuItem}

class HomeMenuBar extends MenuBar {

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

    this.contents += fileMenu
    this.contents += editMenu
}
