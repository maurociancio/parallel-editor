package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class RemoteLoginRefusedResponse(val reason: String)
