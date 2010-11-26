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
import actors.{KernelActor, ClientActor}
import basic.sync.{SynchronizerAdapter, SynchronizerAdapterFactory}
import basic.{BasicDocumentActor, BasicKernel}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock
import ar.noxit.paralleleditor.common.network.SenderActor
import ar.noxit.paralleleditor.common.{EditOperationJupiterSynchronizer, BasicXFormStrategy}
import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.remote.{TerminateActor, NetworkActors, Peer}

@Test
class ClientActorTest extends AssertionsForJUnit {
    @Before
    def setUp = {
    }

    object NullPeer extends Peer {
        def disconnect = {}
    }

    @Test
    def testSessionsInActors: Unit = {
        val k = new BasicKernel
        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        k.sync = synchronizerAdapterFactory

        val ka = new KernelActor(k)
        ka.start

        val gateway = EasyMock.createStrictMock(classOf[SenderActor])
        gateway ! RemoteLoginOkResponse()
        gateway ! RemoteDocumentSubscriptionResponse("new_doc", "content")
        gateway ! TerminateActor()
        EasyMock.replay(gateway)

        val ca = new ClientActor(ka, NullPeer)
        ca.toKernelConverter = new DefaultToKernelConverter
        ca.remoteConverter = new DefaultRemoteMessageConverter
        ca.start
        ca ! NetworkActors(gateway, null)
        ca ! RemoteLoginRequest("username")
        ca ! RemoteNewDocumentRequest("new_doc", "content")

        Thread.sleep(500)

        val docActor = k.documentByTitle("new_doc").get.asInstanceOf[BasicDocumentActor]
        val doc = docActor.document

        Assert.assertTrue(docActor.sessionExists(ca.session))
        Assert.assertEquals(1, doc.subscriberCount)

        ca ! RemoteLogoutRequest()
        Thread.sleep(500)
        Assert.assertFalse(docActor.sessionExists(ca.session))
        Assert.assertEquals(0, doc.subscriberCount)

        EasyMock.verify(gateway)
    }
}
