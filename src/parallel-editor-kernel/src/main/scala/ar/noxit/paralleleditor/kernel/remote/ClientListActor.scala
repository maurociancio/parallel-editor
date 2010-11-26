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

import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.network.DisconnectablePeer
import ar.noxit.paralleleditor.common.remote.Peer
import ar.noxit.paralleleditor.common.BaseActor

/**
 * Mensajes que se envian a la lista de peers
 */

/**
 * Pide al ClientList que agregue a un nuevo client
 */
case class AddClient(val client: Peer)

/**
 * Pide al ClientList actor que termine la conexión con un peer
 */
case class RemoveClient(val client: Peer)

/**
 * Pide al actor ClientList que termine la conexión con todos los peer
 */
case class RemoveAllClients()

/**
 * Actor que mantiene la lista de peers conectados al kernel.
 */
class ClientListActor extends BaseActor with Loggable with DisconnectablePeer {
    private var connectedClients = List[Peer]()

    override def act = {
        loop {
            react {
                case AddClient(newClient) => {
                    trace("Adding client")

                    if (!connectedClients.contains(newClient))
                        connectedClients = newClient :: connectedClients
                    else
                        warn("Peer already exists")
                }
                case RemoveClient(client) => {
                    trace("Removing client")

                    (connectedClients filter {_ == client}) foreach {_ disconnect}
                    connectedClients = connectedClients filter {_ != client}
                }
                case RemoveAllClients() => {
                    trace("Removing al clients, dying")
                    connectedClients foreach {_ disconnect}
                    exit
                }
            }
        }
    }

    def disconnect(client: Peer) =
        this ! RemoveClient(client)
}
