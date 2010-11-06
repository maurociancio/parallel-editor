package ar.noxit.paralleleditor

import common.messages.{RemoteUserLoggedOut, RemoteNewUserLoggedIn}
import converter.DefaultResponseConverter
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._
import ar.noxit.paralleleditor.client.{RegisterRemoteActor, Documents, ClientActor}

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
