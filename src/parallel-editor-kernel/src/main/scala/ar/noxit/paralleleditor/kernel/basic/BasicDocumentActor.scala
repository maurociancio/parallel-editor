package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.{BasicXFormStrategy, EditOperationJupiterSynchronizer}

class BasicDocumentActor(val document: BasicDocument) extends DocumentActor with Loggable {
    // TODO inyectar
    var syncs = Map[Session, EditOperationJupiterSynchronizer]()

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
                    syncs(who).receiveMsg(m, op => {
                        op.executeOn(document)
                        println("DOC: \n" + document.data)

                        syncs.keySet.filter {s => s != who}.foreach(s => {
                            syncs(s).generateMsg(op, m => {
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

                    syncs = syncs + ((who, new EditOperationJupiterSynchronizer(new BasicXFormStrategy)))

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

    protected def removeSession(who: Session) {
        syncs = syncs - who
    }
}
