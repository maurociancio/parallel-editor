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
package ar.noxit.paralleleditor.client

import actors.Actor
import converter.DefaultResponseConverter
import remote.RemoteServerProxy
import java.net.Socket
import ar.noxit.paralleleditor.common.network.SocketNetworkConnection
import ar.noxit.paralleleditor.common.converter.{DefaultRemoteOperationConverter, DefaultMessageConverter}
import ar.noxit.paralleleditor.common.remote.Peer

trait Session {
    def !(msg: Any)
    def close
}

object Session {
    implicit def sessionActor2Session(tuple: (Actor, Peer)): Session =
        new Session {
            private val actor = tuple._1
            private val peer = tuple._2

            def !(msg: Any) = actor ! msg
            def close = peer disconnect
        }
}

trait JSession {
    def send(msg: Any)
    def close
}

object JSession {
    implicit def session2JSession(session: Session): JSession =
        new JSession {
            def send(msg: Any) = session ! msg
            def close = session close
        }
}

trait Documents {
    /**
     * See ClientMessages.scala
     */
    def process(msg: CommandFromKernel)
}

trait LocalClientActorFactory {
    def newLocalClientActor: Actor
}

class InternalClientActorFactory(private val docs: Documents) extends LocalClientActorFactory {
    val clientActor = new ClientActor(docs)
    clientActor.responseConverter = new DefaultResponseConverter
    clientActor.converter = new DefaultMessageConverter(new DefaultRemoteOperationConverter)

    override def newLocalClientActor = clientActor
}

object SessionFactory {
    def newSession(host: String, port: Int, adapter: Documents): Session = {
        val socket = new Socket(host, port)
        val factory = new InternalClientActorFactory(adapter)

        (factory.clientActor, new RemoteServerProxy(new SocketNetworkConnection(socket), factory))
    }

    def newJSession(host: String, port: Int, adapter: Documents): JSession =
        newSession(host, port, adapter)
}
