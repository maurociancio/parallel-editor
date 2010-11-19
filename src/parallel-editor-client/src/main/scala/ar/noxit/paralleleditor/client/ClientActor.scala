package ar.noxit.paralleleditor.client

import ar.noxit.paralleleditor.common.remote.TerminateActor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.messages._
import actors.{TIMEOUT, Actor}
import ar.noxit.paralleleditor.common.converter.MessageConverter
import converter.ResponseConverter
import reflect.BeanProperty
import ar.noxit.paralleleditor._

class ClientActor(private val doc: Documents) extends Actor with Loggable {
    @BeanProperty
    var timeout = 5000
    @BeanProperty
    var converter: MessageConverter = _
    @BeanProperty
    var responseConverter: ResponseConverter = _

    private var remoteKernelActor: Actor = _

    override def act = {
        trace("Waiting for remote kernel actor registration")

        // espero registracion
        remoteKernelActor = receiveWithin(timeout) {
            case RegisterRemoteActor(caller) => {
                trace("Remote kernel actor received, registered ok")
                caller
            }
            case TIMEOUT => doTimeout
        }

        loop {
            trace("Choosing")

            react {
                // requests
                case req: Request => {
                    trace("request received %s", req)
                    remoteKernelActor ! ToKernel(req)
                }
                // response
                case response: Response => {
                    trace("response received %s", response)
                    doc.process(responseConverter.convert(response))
                }

                // operaciones
                case o: RemoteDocumentOperation => {
                    if (sender != remoteKernelActor)
                        remoteKernelActor ! ToKernel(o)
                    else {
                        val m = converter.convert(o.payload)
                        doc.process(ProcessOperation(o.docTitle, m))
                    }
                }

                case Logout() => {
                    trace("Logout request received")
                    doExit
                }
                case TerminateActor() => {
                    trace("terminate actor received")
                    doExit
                }

                case any: Any => {
                    warn("Uknown message received [%s] [caller=%s]", any, sender)
                }
            }
        }
    }

    private def doTimeout = {
        trace("timeout")
        doExit
    }

    private def doExit = {
        if (remoteKernelActor != null)
            remoteKernelActor ! TerminateActor()
        exit
    }
}
