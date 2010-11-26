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

import basic.{BasicKernel, DocumentSessionFactory, BasicDocument}
import messages.{SubscriberLeftDocument, NewSubscriberToDocument, NewUserLoggedIn}
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._
import org.easymock.{IAnswer, EasyMock}

@Test
class DocumentTest extends AssertionsForJUnit {
    @Test
    def testNotification: Unit = {
        // create a new kernel
        val kernel = new BasicKernel

        // log in to the kernel
        val session1 = kernel.login("username1")
        val callback1 = createMock(classOf[UpdateCallback])
        callback1.update(NewUserLoggedIn("username2"))
        callback1.update(NewSubscriberToDocument("username2", "title"))
        callback1.update(SubscriberLeftDocument("username2", "title"))
        replay(callback1)
        session1.installOnUpdateCallback(callback1)

        // another log in
        val session2 = kernel.login("username2")

        // mock doc session factory
        val docSessionFactory = createMock(classOf[DocumentSessionFactory])

        // new document
        val doc = new BasicDocument("title", "initial content", docSessionFactory)

        val docSessionMock2 = createMock(classOf[DocumentSession])
        docSessionMock2.unsubscribe
        expectLastCall().andAnswer(new IAnswer[Unit] {
            def answer: Unit = doc.unsubscribe(session2)
        })
        replay(docSessionMock2)

        // expectations
        EasyMock.expect(docSessionFactory.newDocumentSession(doc, session1)).andReturn(null)
        EasyMock.expect(docSessionFactory.newDocumentSession(doc, session2)).andReturn(docSessionMock2)
        replay(docSessionFactory)

        // subscribe to document
        val docSession1 = doc.subscribe(session1)

        // subscribe to doc
        val docSession2 = doc.subscribe(session2)

        // unsusbscribe
        docSession2.unsubscribe

        // verify the mock
        verify(docSessionFactory, callback1, docSessionMock2)
    }
}
