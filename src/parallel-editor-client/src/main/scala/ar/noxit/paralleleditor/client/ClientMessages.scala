package ar.noxit.paralleleditor.client

import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation

/**
 * Comando desde el kernel
 */
sealed trait CommandFromKernel

case class ProcessOperation(val title: String, val msg: Message[EditOperation]) extends CommandFromKernel

case class DocumentListUpdate(val docs: List[String]) extends CommandFromKernel

case class UserListUpdate(val usernames: Map[String, List[String]]) extends CommandFromKernel

case class DocumentSubscription(val title: String, val initialContent: String) extends CommandFromKernel

case class DocumentSubscriptionAlreadyExists(val offenderTitle: String) extends CommandFromKernel

case class DocumentSubscriptionNotExists(val offenderTitle: String) extends CommandFromKernel

case class DocumentInUse(val docTitle: String) extends CommandFromKernel

case class UsernameTaken() extends CommandFromKernel

case class DocumentDeleted(val docTitle: String) extends CommandFromKernel

case class DocumentDeletionTitleNotExists(val docTitle: String) extends CommandFromKernel

case class DocumentTitleTaken(val offenderTitle: String) extends CommandFromKernel

case class LoginOk() extends CommandFromKernel

case class NewUserLoggedIn(val username: String) extends CommandFromKernel

case class UserLoggedOut(val username: String) extends CommandFromKernel

case class NewSubscriberToDocument(val username: String, val docTitle: String) extends CommandFromKernel

case class SubscriberLeftDocument(val username: String, val docTitle: String) extends CommandFromKernel

case class DocumentTitleNotExists(val offenderTitle: String) extends CommandFromKernel

case class SubscriptionCancelled(val docTitle: String) extends CommandFromKernel

case class ChatMessage(val username: String, val message: String) extends CommandFromKernel
