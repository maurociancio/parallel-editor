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
