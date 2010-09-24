package ar.noxit.paralleleditor.gui

import swing.FlowPanel
import swing.{Label, TextField, Button}

/**
 * Created by IntelliJ IDEA.
 * User: legilioli
 * Date: 24/09/2010
 * Time: 00:40:12
 * To change this template use File | Settings | File Templates.
 */

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