package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.kernel.exceptions.{DocumentSubscriptionNotExistsException, DocumentSubscriptionAlreadyExistsException}
import reflect.BeanProperty

trait Synchronizer {
    def generate(op: EditOperation, send: Message[EditOperation] => Unit)

    def receive(message: Message[EditOperation], apply: EditOperation => Unit)
}

trait SynchronizerFactory {
    def newSynchronizer: Synchronizer
}

class BasicDocumentActor(private val document: BasicDocument, private val syncFactory: SynchronizerFactory) extends DocumentActor with Loggable {
    val title = document.title
    private var syncs = Map[Session, Synchronizer]()

    @BeanProperty
    var timeout: Int = _

    def act = {
        // TODO hacer que termine
        loop {
            trace("Choosing")
            react {
                // procesar
                case ProcessOperation(who, m) => {
                    trace("Operation Received %s", m)

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
                    try {
                        val docSession = document subscribe who
                        val content = document.data

                        syncs = syncs + ((who, newSynchronizer))
                        who notifyUpdate SubscriptionResponse(docSession, content)
                    }
                    catch {
                        case e: DocumentSubscriptionAlreadyExistsException =>
                            who notifyUpdate SubscriptionAlreadyExists(title)
                    }
                }
                // desuscripcion
                case Unsubscribe(who) => {
                    trace("Unsubscribe requested")
                    try {
                        document unsubscribe who
                        removeSession(who)
                    } catch {
                        case e: DocumentSubscriptionNotExistsException =>
                            who notifyUpdate SubscriptionNotExists(title)
                    }
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
