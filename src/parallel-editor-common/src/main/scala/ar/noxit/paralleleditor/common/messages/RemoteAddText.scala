package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class RemoteAddText(val text: String, val startPos: Int)
