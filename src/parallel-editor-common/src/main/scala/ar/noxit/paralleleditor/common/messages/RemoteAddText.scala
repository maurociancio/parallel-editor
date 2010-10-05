package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
abstract case class RemoteOperation

@serializable
case class RemoteAddText(val text: String, val startPos: Int) extends RemoteOperation
