package ar.noxit.paralleleditor.gui

import swing.event.ButtonClicked
import swing._

class ConnectionPanel extends FlowPanel {
    val connectTo = new Label("Connect to:")
    val ip = new TextField("localhost", 20)
    val separation = new Label(":")
    val port = new TextField("5000", 5)
    val username = new TextField("pepe", 5)
    val connect = new Button("Connect")

    contents += connectTo
    contents += ip
    contents += separation
    contents += port
    contents += username
    contents += connect

    listenTo(connect)

    reactions += {
        case e: ButtonClicked => {
            if (connect.text == "Desconectar")
                desconectar()
            else conectar()
        }
    }

    private def conectar() {
        try {
            val host = ip text
            val portNumber = port.text.toInt
            publish(ConnectionRequest(host, portNumber))
            enableControls(false)
        } catch {
            case e: NumberFormatException =>
                Dialog.showMessage(parent = this, message = "El puerto tiene que se un entero")
        }
    }

    private def desconectar() {
        publish(DisconnectionRequest())
        enableControls(true)
    }

    private def enableControls(state: Boolean) {
        List(ip, port, username).foreach{_.enabled = state}
        def getButtonText = {
            if (state == true) "Conectar" else "Desconectar"
        }
        connect.text = getButtonText
    }

    def user = username.text
}
