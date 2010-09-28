package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.logger.Loggable
import scala.actors.Actor
import scala.actors.Actor._

class KernelActor(val kernel: Kernel) extends Actor with Loggable {

    def act = {
        var exit = false
        info("Started")

        loopWhile(!exit) {
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

                case NewDocumentRequest(session, title) => {
                    trace("New Document Requested [%s]", title)

                    kernel.newDocument(session, title)
                }

                case msg: Any => {
                    warn("Unknown message received [%s]", msg)
                }
            }
        }
        // la ejecución nunca llega acá
    }
}
