package ar.noxit.paralleleditor.gui

import scala.swing.{SimpleSwingApplication, MainFrame}
import java.awt.Dimension

object App extends SimpleSwingApplication {
    def top = new MainFrame {
        title = "Parallel Editor GUI"
        size = new Dimension(400, 300)
    }
}