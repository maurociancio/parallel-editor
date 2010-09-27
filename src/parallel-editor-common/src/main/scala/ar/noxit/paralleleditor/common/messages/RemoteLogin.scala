package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class RemoteLogin(val username: String)
