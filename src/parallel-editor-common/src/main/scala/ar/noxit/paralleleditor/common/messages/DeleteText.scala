package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class DeleteText(val startPos: Int, val size: Int)
