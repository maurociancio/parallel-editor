package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class AddText(val text: String, val startPos: Int)
