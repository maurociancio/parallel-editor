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

import actors.KernelActor
import basic.sync.SynchronizerAdapterFactory
import ar.noxit.paralleleditor.common.BasicXFormStrategy
import basic.BasicKernel
import messages.TerminateKernel
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._

@Test
class StopKernelTest extends AssertionsForJUnit {
    var kernel: BasicKernel = _
    var docSession: DocumentSession = _

    @Before
    def setUp = {
        kernel = new BasicKernel;
        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        kernel.sync = synchronizerAdapterFactory
        kernel.timeout = 5000
        docSession = null
    }

    @Test
    def testKernel: Unit = {
        val session = kernel.login("username")

        val callback = createMock(classOf[UpdateCallback])
        callback.update(anyObject())
        replay(callback)

        session.installOnUpdateCallback(callback)

        kernel.newDocument(session, "title", "content")
        val doc = kernel.documentByTitle("title")
        kernel.terminate


        Thread.sleep(1000)
        verify(callback)
    }

    @Test
    def testKernelActor: Unit = {
        val ka = new KernelActor(kernel)
        ka.start
        ka ! TerminateKernel()

        Thread.sleep(1000)
    }
}
