package ar.noxit.paralleleditor.common.messages

import scala.serializable

@serializable
case class RemoteDocumentListResponse(val docList: List[String])
