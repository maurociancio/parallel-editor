package ar.noxit.paralleleditor.gui

import swing.event.Event

case class ConnectionRequest(val host: String, val port: Int) extends Event
case class InsertionEvent(val pos:Int, val text:String) extends Event
case class DeletionEvent(val pos:Int, count:Int) extends Event
