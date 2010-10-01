package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.kernel.network.{MessageInput, MessageOutput}
import actors.{Actor, TIMEOUT}
import ar.noxit.paralleleditor.common.logger.Loggable

abstract class BaseNetworkActor extends Actor with Loggable {
    protected var client: Actor = _
    protected val timeout = 5000

    override def act = {
        client = receiveClient
    }

    private def receiveClient = {
        receiveWithin(timeout) {
            case SetClientActor(client) => {
                trace("client actor received")
                client
            }
            case TerminateActor() => doExit
            case TIMEOUT => doExit
        }
    }

    protected def doExit = {
        if (client != null)
            client ! TerminateActor()
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

            client ! inputMessage
        }
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
                case TerminateActor() => {
                    trace("Exit received, exiting")
                    doExit
                }
                case message: Any => {
                    trace("writing message to client [%s]", message)
                    try {
                        output writeMessage message
                    }
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
}
