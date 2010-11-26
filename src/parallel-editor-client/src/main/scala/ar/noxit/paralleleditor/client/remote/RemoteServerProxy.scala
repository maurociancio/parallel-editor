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
package ar.noxit.paralleleditor.client.remote

import actors.TIMEOUT
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.network._
import ar.noxit.paralleleditor.common.remote.{TerminateActor, NetworkActors, BasePeerProxy, Peer}
import ar.noxit.paralleleditor.client.{ToKernel, RegisterRemoteActor, FromKernel, LocalClientActorFactory}
import ar.noxit.paralleleditor.common.messages.RemoteLogoutRequest
import ar.noxit.paralleleditor.common.BaseActor

object NullDisconnectablePeer extends DisconnectablePeer {

    def disconnect(peer: Peer) = {
    }
}

class RemoteServerProxy(private val networkConnection: NetworkConnection,
                        private val clientActorFactory: LocalClientActorFactory)
        extends BasePeerProxy(networkConnection, NullDisconnectablePeer) {

    protected def newNetworkListener(input: MessageInput) = new NetworkListenerActor(input) {

        override protected def newThread = new NetworkListenerThread(input, peer) {
            override protected def onNewMessage(inputMessage: Any) =
                peer ! FromKernel(inputMessage)
        }
    }

    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newClient() = new RemoteKernelActor(clientActorFactory, this)
}

class RemoteKernelActor(private val clientActorFactory: LocalClientActorFactory,
                        private val peer: Peer) extends BaseActor with Loggable {

    val localClientActor = clientActorFactory.newLocalClientActor.start
    val timeout = 5000

    private var listener: SenderActor = _
    private var gateway: SenderActor = _

    override def act = {
        trace("Sending registration to local client")

        val (gateway, listener) = receiveNetworkActors
        this.listener = listener
        this.gateway = gateway

        localClientActor ! RegisterRemoteActor(this)

        loop {
            react {
                case ToKernel(msg) => {
                    trace("Received message to kernel %s", msg)
                    gateway ! msg
                }
                case FromKernel(msg) => {
                    trace("Received message from kernel %s", msg)
                    localClientActor ! msg
                }
                case TerminateActor() => {
                    gateway ! RemoteLogoutRequest()
                    doExit
                }
                case any: Any =>
                    trace("Unknown message received %s", any)
            }
        }
    }

    private def receiveNetworkActors = {
        trace("Waiting for actors")

        receiveWithin(timeout) {
            case NetworkActors(gateway, listener) => {
                trace("network actors received")
                (gateway, listener)
            }
            case TIMEOUT => doExit
        }
    }

    private def doExit = {
        trace("Terminating")

        if (gateway != null)
            gateway ! TerminateActor()
        if (listener != null)
            listener ! TerminateActor()
        peer.disconnect

        exit
    }
}
