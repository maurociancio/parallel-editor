package ar.noxit.paralleleditor.gui

import scala.swing.{MenuBar,Menu,MenuItem}
/**
 * Created by IntelliJ IDEA.
 * User: legilioli
 * Date: 21/09/2010
 * Time: 19:59:40
 * To change this template use File | Settings | File Templates.
 */

class BarraMenuesGUI extends MenuBar {

    val menuArchivo = new Menu("Archivo") {
      contents += new MenuItem("Abrir")
      contents += new MenuItem("Guardar")
      contents += new MenuItem("Guardar Como")
      contents += new MenuItem("Salir")
    }

    val menuEdicion  = new Menu("Edicion") {
      contents += new MenuItem("Copiar")
      contents += new MenuItem("Cortar")
      contents += new MenuItem("Pegar")
      contents += new MenuItem("Buscar...")

    }

    this.contents += menuArchivo
    this.contents += menuEdicion

}
