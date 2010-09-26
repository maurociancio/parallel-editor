package ar.noxit.paralleleditor.gui

import swing.FlowPanel
import swing.{Label, TextField, Button}

class ConnectionPanel extends FlowPanel {
    val connectTo = new Label("Connect to:")
    val ip = new TextField("ip", 20)
    val separation = new Label(":")
    val port = new TextField("port", 5)
    val connect = new Button("Connect")

    contents += connectTo
    contents += ip
    contents += separation
    contents += port
    contents += connect
}