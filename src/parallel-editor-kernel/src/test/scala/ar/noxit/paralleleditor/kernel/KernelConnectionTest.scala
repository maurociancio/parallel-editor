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
package ar.noxit.paralleleditor.kernel

import actors.converter.{DefaultRemoteMessageConverter, DefaultToKernelConverter}
import ar.noxit.paralleleditor.common.converter._
import actors.{KernelActor, ClientActor}
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import basic.sync.SynchronizerAdapterFactory
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import scala.actors.Actor._
import ar.noxit.paralleleditor.common.messages._
import Assert._
import ar.noxit.paralleleditor.common.remote.{NetworkActors, Peer}
import scala.actors.{Future, Actor}
import ar.noxit.paralleleditor.common.BasicXFormStrategy

object NullPeer extends Peer {
    def disconnect = {}
}

@Test
class KernelConnectionTest extends AssertionsForJUnit {
    var kernel: BasicKernel = _
    var client: ClientActor = _
    var ka: Actor = _
    var remoteEchoClient: Actor = _
    val converter = new DefaultRemoteDocumentOperationConverter(new DefaultSyncOperationConverter(new DefaultEditOperationConverter))

    @Before
    def setUp: Unit = {
        remoteEchoClient = actor {
            loop {
                receive {
                    case any => println("null actor received " + any)
                }
            }
        }
        kernel = new BasicKernel
        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        kernel.sync = synchronizerAdapterFactory
        kernel.timeout = 5000
        ka = new KernelActor(kernel).start
        client = new ClientActor(ka, NullPeer)
        client.remoteDocOpconverter = converter
        client.toKernelConverter = new DefaultToKernelConverter
        client.remoteConverter = new DefaultRemoteMessageConverter
        client.messageConverter = new DefaultMessageConverter(new DefaultRemoteOperationConverter)
        client.start
        client ! NetworkActors(remoteEchoClient, remoteEchoClient)
    }

    @Test
    def testSessionCount: Unit = {
        client ! RemoteLoginRequest("myUsername")
        Thread.sleep(300)

        assertEquals(kernel.sessionCount, 1)

        client ! RemoteLogoutRequest()
        Thread.sleep(300)

        assertEquals(kernel.sessionCount, 0)
    }

    @Test
    def testDocumentCount: Unit = {
        client ! RemoteLoginRequest("myUsername")
        client ! RemoteNewDocumentRequest("title")
        Thread.sleep(300)

        assertEquals(kernel.documentCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 1)

        client ! RemoteLogoutRequest()
        Thread.sleep(300)

        assertEquals(kernel.documentCount, 1)
        assertEquals(calculateSubscriberCount(kernel.documentSubscriberCount("title")), 0)
    }

    @Test
    def test2Clients: Unit = {
        val client2 = new ClientActor(ka, NullPeer)
        client2.remoteDocOpconverter = converter
        client2.toKernelConverter = new DefaultToKernelConverter
        client2.remoteConverter = new DefaultRemoteMessageConverter
        client2.messageConverter = new DefaultMessageConverter(new DefaultRemoteOperationConverter)
        client2.start

        var docList: List[String] = null

        client2 ! NetworkActors(actor {
            loop {
                receive {
                    case RemoteDocumentListResponse(doc) => {
                        docList = doc
                    }
                    case a: Any => {}
                }
            }
        }, remoteEchoClient)

        client ! RemoteLoginRequest("myUsername")
        client ! RemoteNewDocumentRequest("title")
        client2 ! RemoteLoginRequest("myUsername2")
        client2 ! RemoteDocumentListRequest()

        Thread.sleep(1000)

        assertEquals(List("title"), docList)
        assertEquals(2, kernel.sessionCount)
    }

    def calculateSubscriberCount(count: Option[Future[Int]]) = {
        Thread.sleep(300)
        count.get.apply
    }
}
