/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.common.network

import actors.{Actor, TIMEOUT}
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.BaseActor
import ar.noxit.paralleleditor.common.remote.{Peer, TerminateActor, SetPeerActor}

abstract class BaseNetworkActor extends BaseActor with Loggable {
    protected var peer: Actor = _
    protected val timeout = 5000

    override def act = peer = receiveClient

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

class NetworkListenerThread(val input: MessageInput, val peer: Actor) extends Thread with Loggable {
    setName("NetworkListenerThread")
    override def run = {
        try
            processMessages
        catch {
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

    protected def doExit = {
        trace("terminating")
        if (peer != null)
            peer ! TerminateActor()
    }
}

/**
 * Ver RemoteClientProxy ScalaDoc
 */
class NetworkListenerActor(private val input: MessageInput) extends BaseNetworkActor {
    override def act = {
        super.act

        // spawn a new thread to avoid blocking the current thread
        newThread.start
    }

    protected def newThread: Thread =
        new NetworkListenerThread(input, peer)
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
