package ar.noxit.paralleleditor.kernel.messages

case class RemoteLogin(val username: String)
case class RemoteLogoutRequest
case class RemoteLoginOkResponse
case class RemoteNewDocumentRequest(val title: String)
case class RemoteNewDocumentOkResponse
case class RemoteDocumentList
case class RemoteDocumentListResponse(val docList: List[String])