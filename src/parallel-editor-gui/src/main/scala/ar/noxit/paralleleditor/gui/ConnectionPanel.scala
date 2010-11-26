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
