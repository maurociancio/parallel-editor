package ar.noxit.paralleleditor.kernel.messages

import scala.serializable

@serializable
case class RemoteLogin(val username: String)

@serializable
case class RemoteLogoutRequest

@serializable
case class RemoteLoginOkResponse

@serializable
case class RemoteNewDocumentRequest(val title: String)

@serializable
case class RemoteNewDocumentOkResponse

@serializable
case class RemoteDocumentList

@serializable
case class RemoteDocumentListResponse(val docList: List[String])