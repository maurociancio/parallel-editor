package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation
import reflect.BeanProperty
import ar.noxit.paralleleditor.kernel.exceptions.{DocumentInUseException, DocumentSubscriptionNotExistsException, DocumentSubscriptionAlreadyExistsException}

trait Synchronizer {
    def generate(op: EditOperation, send: Message[EditOperation] => Unit)

    def receive(message: Message[EditOperation], apply: EditOperation => Unit)
}

trait SynchronizerFactory {
    def newSynchronizer: Synchronizer
}

class BasicDocumentActor(val document: BasicDocument, private val syncFactory: SynchronizerFactory) extends DocumentActor with Loggable {
    val title = document.title
    private var syncs = Map[Session, Synchronizer]()

    @BeanProperty
    var timeout: Int = _

    def act = {

        loop {
            trace("Choosing")
            react {
                // procesar
                case ProcessOperation(who, m) => {
                    trace("Operation Received %s", m)

                    syncs(who).receive(m, op => {
                        op.executeOn(document)

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

                //cerrar documentow
                case Close(who) => {
                    try {
                        document.delete(who)
                        who notifyUpdate DocumentDeleted(title)
                        who notifyUpdate DocumentDeletedOk(title)
                        exit
                    }
                    catch {
                        case e: DocumentSubscriptionNotExistsException =>
                            who notifyUpdate SubscriptionNotExists(title)
                        case e: DocumentInUseException =>
                            who notifyUpdate DocumentInUse(title)
                    }
                }

                case any: Any => warn("Unknown message received %s", any)
            }
        }
    }

    protected def newSynchronizer: Synchronizer = syncFactory.newSynchronizer

    protected def removeSession(who: Session) {
        syncs = syncs - who
    }

    def sessionExists(session: Session) =
        syncs.contains(session)
}
