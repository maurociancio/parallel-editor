package ar.noxit.paralleleditor

import common.Message
import common.operation.EditOperation

/**
 * Comando desde el kernel
 */
sealed trait FromKernel

case class ProcessOperation(val title: String, val msg: Message[EditOperation]) extends FromKernel

case class DocumentListUpdate(val docs: List[String]) extends FromKernel

case class UserListUpdate(val usernames: Map[String, List[String]]) extends FromKernel

case class DocumentSubscription(val title: String, val initialContent: String) extends FromKernel

case class DocumentSubscriptionAlreadyExists(val offenderTitle: String) extends FromKernel

case class DocumentSubscriptionNotExists(val offenderTitle: String) extends FromKernel

case class DocumentInUse(val docTitle: String) extends FromKernel

case class UsernameTaken() extends FromKernel

case class DocumentDeleted(val docTitle: String) extends FromKernel

case class DocumentDeletionTitleNotExists(val docTitle: String) extends FromKernel

case class DocumentTitleTaken(val offenderTitle: String) extends FromKernel

case class LoginOk() extends FromKernel

case class NewUserLoggedIn(val username: String) extends FromKernel

case class UserLoggedOut(val username: String) extends FromKernel

case class NewSubscriberToDocument(val username: String, val docTitle: String) extends FromKernel

case class SubscriberLeftDocument(val username: String, val docTitle: String) extends FromKernel
