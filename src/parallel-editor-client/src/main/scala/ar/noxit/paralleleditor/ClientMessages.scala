package ar.noxit.paralleleditor

import common.Message
import common.operation.EditOperation

case class ProcessOperation(val title: String, val msg: Message[EditOperation])

case class DocumentListUpdate(val docs: List[String])

case class DocumentSubscription(val title: String, val initialContent: String)

case class DocumentSubscriptionAlreadyExists(val offenderTitle: String)

case class DocumentSubscriptionNotExists(val offenderTitle: String)

case class UsernameTaken()

case class DocumentTitleTaken(val offenderTitle: String)

case class LoginOk
