package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.{Message, BasicXFormStrategy, EditOperationJupiterSynchronizer}
import ar.noxit.paralleleditor.common.operation.EditOperation
import sync.SynchronizerAdapter

trait Synchronizer {
    def generate(op: EditOperation, send: Message[EditOperation] => Unit)
    def receive(message: Message[EditOperation], apply: EditOperation => Unit)
}

class BasicDocumentActor(val document: BasicDocument) extends DocumentActor with Loggable {
    var syncs = Map[Session, Synchronizer]()

    // TODO inyectar
    private val timeout = 5000
    val title = document.title

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

                        syncs.keySet.filter {s => s != who}.foreach(s => {
                            syncs(s).generate(op, m => {
                                s.notifyUpdate(PublishOperation(title, m))
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

    protected def newSynchronizer: Synchronizer =
        new SynchronizerAdapter(new EditOperationJupiterSynchronizer(new BasicXFormStrategy))

    protected def removeSession(who: Session) {
        syncs = syncs - who
    }
}
