package ar.noxit.paralleleditor

import common.Message
import common.operation.EditOperation

case class ProcessOperation(val title: String, val msg: Message[EditOperation])

case class DocumentListUpdate(val docs: List[String])

case class DocumentSubscription(val title: String, val initialContent: String)

case class UsernameTaken()
