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

import basic.BasicKernel
import messages.ChatMessage
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._

class ChatTest extends AssertionsForJUnit {
    @Before
    def setUp = {
    }

    @Test
    def testChatBetweenTwoUsers: Unit = {
        val k = new BasicKernel

        val s1 = k.login("username1")
        val s2 = k.login("username2")

        val cb1 = createMock(classOf[UpdateCallback])
        val cb2 = createMock(classOf[UpdateCallback])
        cb2 update ChatMessage(s1, "hello there")
        cb1 update ChatMessage(s2, "hello again")
        replay(cb1, cb2)

        s1.installOnUpdateCallback(cb1)
        s2.installOnUpdateCallback(cb2)

        k.chat(s1, "hello there")
        k.chat(s2, "hello again")

        verify(cb1, cb2)
    }
}
