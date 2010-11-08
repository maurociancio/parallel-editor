package ar.noxit.paralleleditor

import common.Message
import common.operation.EditOperation

case class ProcessOperation(val title: String, val msg: Message[EditOperation])

case class DocumentListUpdate(val docs: List[String])

case class UserListUpdate(val usernames: Map[String, List[String]])

case class DocumentSubscription(val title: String, val initialContent: String)

case class DocumentSubscriptionAlreadyExists(val offenderTitle: String)

case class DocumentSubscriptionNotExists(val offenderTitle: String)

case class DocumentInUse(val docTitle: String)

case class UsernameTaken()

case class DocumentDeleted(val docTitle: String)

case class DocumentDeletionTitleNotExists(val docTitle: String)

case class DocumentTitleTaken(val offenderTitle: String)

case class LoginOk()

case class NewUserLoggedIn(val username: String)

case class UserLoggedOut(val username: String)
