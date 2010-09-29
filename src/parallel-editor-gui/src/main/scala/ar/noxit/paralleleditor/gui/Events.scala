package ar.noxit.paralleleditor.gui

import swing.event.Event


case class ConnectionRequest(val host: String, val port: Integer) extends Event
case class InsertionEvent(val pos:Integer, val text:String) extends Event
case class DeletionEvent(val pos:Integer, count:Integer) extends Event