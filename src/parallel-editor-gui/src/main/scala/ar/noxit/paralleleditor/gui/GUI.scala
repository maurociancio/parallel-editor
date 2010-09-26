package ar.noxit.paralleleditor.gui

import scala.swing._

object GUI extends SimpleSwingApplication {
    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new BarraMenuesGUI

        contents = new BoxPanel(Orientation.Vertical) {
            contents += new ScrollableTextArea
            contents += new ConnectionPanel
        }
    }
}