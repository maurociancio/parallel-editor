package ar.noxit.paralleleditor.gui

import swing.FlowPanel
import swing.{Label, TextField, Button}
import swing.event.ButtonClicked

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
        val host = ip text
        val portNumber = port.text.toInt
        ConnectionPanel.this.publish(ConnectionRequest(host, portNumber))

        enableControls(false)
    }

    private def desconectar() {
        //trace("disconnection requested")
        publish(DisconnectionRequest())
        enableControls(true)
    }

    private def enableControls(state: Boolean) {
        List(ip, port, username).foreach(_.enabled = state)
        def getButtonText = {
            if (state == true) "Conectar" else "Desconectar"
        }
        connect.text = getButtonText
    }

    def user = username.text
}
