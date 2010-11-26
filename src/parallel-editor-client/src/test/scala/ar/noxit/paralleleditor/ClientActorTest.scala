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
package ar.noxit.paralleleditor

import client._
import client.converter.DefaultResponseConverter
import common.messages.{RemoteUserLoggedOut, RemoteNewUserLoggedIn}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._

@Test
class ClientActorTest extends AssertionsForJUnit {

    @Test
    def test: Unit = {
        val docs = createMock(classOf[Documents])
        docs.process(NewUserLoggedIn("user"))
        docs.process(UserLoggedOut("user"))
        replay(docs)

        val client = new ClientActor(docs)
        client.responseConverter = new DefaultResponseConverter
        client.start

        client ! RegisterRemoteActor(null)
        client ! RemoteNewUserLoggedIn("user")
        client ! RemoteUserLoggedOut("user")

        Thread.sleep(1000)
    }
}
