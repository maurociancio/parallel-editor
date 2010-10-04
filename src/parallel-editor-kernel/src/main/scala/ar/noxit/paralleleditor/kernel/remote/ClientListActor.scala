package ar.noxit.paralleleditor.kernel.remote

import actors.Actor
import ar.noxit.paralleleditor.common.logger.Loggable
import ar.noxit.paralleleditor.common.network.DisconnectClientCallback
import ar.noxit.paralleleditor.common.remote.Client

case class AddClient(val client: Client)
case class RemoveClient(val client: Client)
case class RemoveAllClients

class ClientListActor extends Actor with Loggable with DisconnectClientCallback {
    private var connectedClients = List[Client]()

    override def act = {
        loop {
            react {
                case AddClient(newClient) => {
                    trace("Adding client")

                    if (!connectedClients.contains(newClient))
                        connectedClients = newClient :: connectedClients
                    else
                        warn("Client already exists")
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

    def disconnect(client: Client) = {
        this ! RemoveClient(client)
    }
}
