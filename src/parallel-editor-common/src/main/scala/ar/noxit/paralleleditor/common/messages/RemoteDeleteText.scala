package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class RemoteDeleteText(val startPos: Int, val size: Int) extends RemoteOperation
