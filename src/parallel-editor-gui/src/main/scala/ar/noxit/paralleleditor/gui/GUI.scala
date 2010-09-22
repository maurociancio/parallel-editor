package ar.noxit.paralleleditor.gui

import swing._
import scala.swing.event.ValueChanged

object GUI extends SimpleSwingApplication {
    def top = new MainFrame {
        title = "Parallel Editor GUI"
        menuBar = new BarraMenuesGUI

        contents = new FlowPanel {
            val textArea = new TextArea("hola mundo", 20, 50)
            contents += textArea

            listenTo(textArea)

            reactions += {
                case vc: ValueChanged => {
                    println(vc)
                }
            }
        }
    }
}