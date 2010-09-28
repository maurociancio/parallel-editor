package ar.noxit.paralleleditor.kernel.basic

import actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.EditOperation
import ar.noxit.paralleleditor.kernel.messages._

class BasicDocumentActor(documentFactory: DocumentFactory) extends DocumentActor with Loggable {
    val document = documentFactory.newBasicDocument(this)
    val title = document.title

    def act = {
        var exit = false

        loopWhile(!exit) {
            trace("Choosing")
            react {
                case SilentUnsubscribe(session) => {
                    trace("Silent unsubscribe received")
                    document silentUnsubscribe session
                }
                case SubscriberCount() => {
                    trace("Subscriber Count Request")
                    reply(document subscriberCount)
                }
                case Subscribe(who) => {
                    trace("Subscribe requested")
                    val docSession = document subscribe who
                    who notifyUpdate NewDocumentResponse(docSession)
                }
                case Unsubscribe(who) => {
                    trace("Unsubscribe requested")
                    document unsubscribe who
                }
                case operation: EditOperation => {
                    trace("Operation Received %s", operation)
                    operation.executeOn(document)
                }
                case any: Any => warn("Unknown message received %s", any)
            }
        }
    }
}
