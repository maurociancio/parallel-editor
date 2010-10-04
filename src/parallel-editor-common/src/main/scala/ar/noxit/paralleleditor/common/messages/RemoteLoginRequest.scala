package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class RemoteLoginRequest(val username: String)
