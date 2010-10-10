package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.{BasicXFormStrategy, EditOperationJupiterSynchronizer}

class BasicDocumentActor(val document: BasicDocument) extends DocumentActor with Loggable {
    // TODO inyectar
    val sync = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
    // TODO inyectar
    private val timeout = 5000
    val title = document.title

    def act = {
        // TODO hacer que termine
        loop {
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
                    val content = document.data
                    who notifyUpdate SubscriptionResponse(docSession, content)
                }
                case Unsubscribe(who) => {
                    trace("Unsubscribe requested")
                    document unsubscribe who
                }
                case ProcessOperation(who, operation) => {
                    trace("Operation Received %s", operation)
                    operation.executeOn(document)
                    document.propagateOperation(who, operation)
                }
                case any: Any => warn("Unknown message received %s", any)
            }
        }
    }
}
