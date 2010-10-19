package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.logger.Loggable
import scala.actors.Actor

class KernelActor(val kernel: Kernel) extends Actor with Loggable {
    def act = {
        info("Started")

        // TODO hacer que salga
        loop {
            trace("Choosing")
            react {
                case LoginRequest(username) => {
                    trace("Login Requested by=[%s]", username)

                    // TODO catch user already login exception and send it to
                    // the caller, so he could pick a new name
                    val newSession = kernel.login(username)

                    trace("Sending Login Response")
                    sender ! LoginResponse(newSession)
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
                    kernel.subscribe(session, title)
                }

                case NewDocumentRequest(session, title, initialContent) => {
                    trace("New Document Requested [%s]", title)

                    kernel.newDocument(session, title, initialContent)
                }

                case msg: Any => {
                    warn("Unknown message received [%s]", msg)
                }
            }
        }
        // la ejecución nunca llega acá
    }
}
