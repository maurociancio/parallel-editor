package ar.noxit.paralleleditor.gui

import swing.event.Event

case class ConnectionRequest(val host: String, val port: Integer) extends Event
