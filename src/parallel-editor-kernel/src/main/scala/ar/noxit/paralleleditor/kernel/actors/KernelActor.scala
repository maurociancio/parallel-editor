package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.logger.Loggable
import exceptions.{DocumentTitleNotExistsException, DocumentDeleteUnexistantException, DocumentTitleAlreadyExitsException, UsernameAlreadyExistsException}
import ar.noxit.paralleleditor.common.BaseActor

class KernelActor(val kernel: Kernel) extends BaseActor with Loggable {
    def act = {
        info("Started")

        loop {
            trace("Choosing")
            react {
                case LoginRequest(username) => {
                    trace("Login Requested by=[%s]", username)

                    try {
                        val newSession = kernel.login(username)

                        trace("Sending Login Response")
                        sender ! LoginResponse(newSession)
                    }
                    catch {
                        case e: UsernameAlreadyExistsException =>
                            sender ! UsernameAlreadyExists()
                    }
                }

                case DocumentListRequest(session) => {
                    trace("Document List Requested")

                    // ask kernel for the document list
                    val documentList = kernel.documentList

                    // return it to the caller
                    session notifyUpdate DocumentListResponse(documentList)
                }
                case SubscribeToDocumentRequest(session, title) => {
                    trace("subscribe to document")

                    try
                        kernel.subscribe(session, title)
                    catch {
                        case e: DocumentTitleNotExistsException =>
                            sender ! DocumentNotExists(title)
                    }
                }

                case NewDocumentRequest(session, title, initialContent) => {
                    trace("New Document Requested [%s]", title)

                    try
                        kernel.newDocument(session, title, initialContent)
                    catch {
                        case e: DocumentTitleAlreadyExitsException =>
                            sender ! DocumentTitleExists(title)
                    }
                }

                case UserListRequest(session) => {
                    trace("UserListRequest")
                    kernel.userList(session)
                }

                case ChatMessage(session, message) => {
                    trace("chat message")
                    kernel.chat(session, message)
                }

                case CloseDocument(session, docTitle) => {
                    trace("delete document")
                    try
                        kernel.deleteDocument(session, docTitle)
                    catch {
                        case e: DocumentDeleteUnexistantException =>
                            sender ! DocumentDeletionTitleNotExists(docTitle)
                    }
                }

                case DocumentDeleted(title) => {
                    trace("document deleted")
                    kernel.removeDeletedDocument(title)
                }

                case TerminateKernel() => {
                    trace("terminate kernel received")
                    kernel.terminate
                    doExit
                }

                case msg: Any => {
                    warn("Unknown message received [%s]", msg)
                }
            }
        }
        // la ejecución nunca llega acá
    }

    protected def doExit = {
        trace("exiting")
        exit
    }
}
