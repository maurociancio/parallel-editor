package ar.noxit.paralleleditor.common.network

import actors.{Actor, TIMEOUT}
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.remote.{TerminateActor, SetPeerActor}

abstract class BaseNetworkActor extends Actor with Loggable {
    protected var peer: Actor = _
    protected val timeout = 5000

    override def act = {
        peer = receiveClient
    }

    private def receiveClient = {
        receiveWithin(timeout) {
            case SetPeerActor(client) => {
                trace("client actor received")
                client
            }
            case TerminateActor() => doExit
            case TIMEOUT => doExit
        }
    }

    protected def doExit = {
        trace("terminating")
        if (peer != null)
            peer ! TerminateActor()
        exit
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class NetworkListenerActor(private val input: MessageInput) extends BaseNetworkActor {
    override def act = {
        super.act

        try {
            processMessages
        } catch {
            case e: Exception => {
                warn(e, "Exception thrown during receive")
                doExit
            }
        }
    }

    private def processMessages: Unit = {
        while (true) {
            val inputMessage: Any = input.readMessage
            trace("Message received %s", inputMessage)

            onNewMessage(inputMessage)
        }
    }

    protected def onNewMessage(inputMessage: Any) {
        peer ! inputMessage
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class GatewayActor(private val output: MessageOutput) extends BaseNetworkActor {
    override def act = {
        super.act

        loop {
            react {
                case TerminateActor() =>
                    doExit
                case message: Any => {
                    trace("writing message to client [%s]", message)
                    try
                        onNewMessage(message)
                    catch {
                        case e: Exception => {
                            warn(e, "Exception thrown during send")
                            doExit
                        }
                    }
                }
            }
        }
    }

    protected def onNewMessage(message: Any) {
        output writeMessage message
    }
}
