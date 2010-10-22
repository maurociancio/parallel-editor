package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation

trait Synchronizer {
    def generate(op: EditOperation, send: Message[EditOperation] => Unit)

    def receive(message: Message[EditOperation], apply: EditOperation => Unit)
}

trait SynchronizerFactory {
    def newSynchronizer: Synchronizer
}

class BasicDocumentActor(val document: BasicDocument, val syncFactory: SynchronizerFactory) extends DocumentActor with Loggable {
    val title = document.title
    var syncs = Map[Session, Synchronizer]()

    var timeout : Int = _

    def act = {
        // TODO hacer que termine
        loop {
            trace("Choosing")
            react {
                // procesar
                case ProcessOperation(who, m) => {
                    trace("Operation Received %s", m)

                    // TODO capturar excepciones
                    syncs(who).receive(m, op => {
                        op.executeOn(document)
                        println("DOC: \n" + document.data)

                        document.propagateToOthers(who, other => {
                            syncs(other).generate(op, m => {
                                other.notifyUpdate(PublishOperation(title, m))
                            })
                        })
                    })
                }
                // cantidad
                case SubscriberCount() => {
                    trace("Subscriber Count Request")
                    reply(document subscriberCount)
                }
                // suscripcion
                case Subscribe(who) => {
                    trace("Subscribe requested")
                    // TODO catchear excepciones
                    val docSession = document subscribe who
                    val content = document.data

                    syncs = syncs + ((who, newSynchronizer))

                    who notifyUpdate SubscriptionResponse(docSession, content)
                }
                // desuscripcion
                case Unsubscribe(who) => {
                    trace("Unsubscribe requested")
                    document unsubscribe who
                    removeSession(who)
                }
                case SilentUnsubscribe(session) => {
                    trace("Silent unsubscribe received")
                    document silentUnsubscribe session
                    removeSession(session)
                }
                case any: Any => warn("Unknown message received %s", any)
            }
        }
    }

    protected def newSynchronizer: Synchronizer = syncFactory.newSynchronizer

    protected def removeSession(who: Session) {
        syncs = syncs - who
    }
}
