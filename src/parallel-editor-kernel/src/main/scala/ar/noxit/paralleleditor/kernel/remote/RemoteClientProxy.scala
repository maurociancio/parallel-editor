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
package ar.noxit.paralleleditor.kernel.remote

import ar.noxit.paralleleditor.common.remote.{PeerActorFactory, BasePeerProxy}
import ar.noxit.paralleleditor.common.network._

class RemoteClientProxy(private val nc: NetworkConnection,
                        private val factory: PeerActorFactory,
                        private val callback: DisconnectablePeer)
        extends BasePeerProxy(nc, callback) {
    protected def newGateway(output: MessageOutput) = new GatewayActor(output)

    protected def newNetworkListener(input: MessageInput) = new NetworkListenerActor(input)

    protected def newClient() = factory.newClientActor(this)
}
