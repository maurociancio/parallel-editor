package ar.noxit.paralleleditor.gui

import scala.swing._
import scala.swing.event.ValueChanged


object GUI extends SimpleSwingApplication {
    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new BarraMenuesGUI

        contents = new BoxPanel(Orientation.Vertical) {

            val textArea = new ScrollableTextArea
            contents += textArea
            contents += new ConnectionPanel

            listenTo(textArea)
/*
            reactions += {
                case vc: ValueChanged => {
                    println(vc)
                }
            }*/
        }
    }
}