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
package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.exceptions._
import ar.noxit.paralleleditor.kernel._
import messages.{SubscriptionCancelled, SubscriberLeftDocument, NewSubscriberToDocument}
import scala.List
import ar.noxit.paralleleditor.common.operation.{Caret, DocumentData}

class BasicDocument(val title: String, var data: String, private val docSessionFactory: DocumentSessionFactory) extends Document with DocumentData {
    private var subscribers = List[Session]()

    override def subscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (subscribers contains session)
            throw new DocumentSubscriptionAlreadyExistsException("the session is already suscribed to this document")

        // notify users
        subscribers.foreach {target => target notifyUpdate NewSubscriberToDocument(session.username, title)}

        // add the new session
        subscribers = session :: subscribers

        // new doc session
        docSessionFactory.newDocumentSession(this, session)
    }

    def unsubscribe(session: Session) = {
        if (session == null)
            throw new IllegalArgumentException("unexpected null session")

        if (!subscribers.contains(session))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers = subscribers filter {_ != session}

        // notify the subscribers
        subscribers.foreach {target => target notifyUpdate SubscriberLeftDocument(session.username, title)}

        session notifyUpdate SubscriptionCancelled(title)
    }

    def silentUnsubscribe(session: Session) = {
        try {
            this unsubscribe session
        } catch {
            case e: DocumentSubscriptionNotExistsException => null
        }
    }

    def propagateToOthers(who: Session, what: Session => Unit) = {
        if (!subscribers.contains(who))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        subscribers.filter {s => s != who}.foreach {
            s => what(s)
        }
    }

    def delete(who: Session) = {
        if (!subscribers.contains(who))
            throw new DocumentSubscriptionNotExistsException("the session is not suscribed to this document")

        if (subscribers.size != 1)
            throw new DocumentInUseException("document is being used")

        subscribers = List()
    }

    val caret = new Caret {
        val selectionLength = 0 // caret at pos 0
        val offset = 0 // nothing selected

        override def change(offset: Int, selectionLength: Int) = null // do nothing}
    }


    override def replace(offset: Int, length: Int, newText: String) =
        data = data.substring(0, offset) + (if (newText == null) "" else newText) + "" + data.substring(offset + length)

    def subscriberCount = subscribers size

    def subscribersNames = subscribers.map {s => s.username}
}
