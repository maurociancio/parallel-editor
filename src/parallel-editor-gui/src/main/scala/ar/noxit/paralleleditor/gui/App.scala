package ar.noxit.paralleleditor.gui

import java.awt.Dimension
import scala.swing._
import scala.swing.event._

object App extends SimpleSwingApplication {
    def top = new MainFrame {
        title = "Parallel Editor GUI"
        size = new Dimension(400, 300)

        val connectTo = new Label("Connect to:")
        val ip = new TextField("ip", 20)
        val separation = new Label(":")
        val port = new TextField("port", 5)
        val connect = new Button("Connect")

        contents = new FlowPanel {
            contents += connectTo
            contents += ip
            contents += separation
            contents += port
            contents += connect
        }

        listenTo(connect)
        reactions += {
            case ButtonClicked(connect) =>
                println("clicked")
        }
    }
}