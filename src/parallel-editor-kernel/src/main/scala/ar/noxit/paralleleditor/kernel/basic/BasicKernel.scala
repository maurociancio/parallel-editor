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

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.common.logger.Loggable
import docsession.BasicDocumentSessionFactory
import exceptions._
import messages._
import scala.List
import reflect.BeanProperty

class BasicKernel extends Kernel with Loggable {
    @BeanProperty
    var timeout: Int = _
    @BeanProperty
    var sync: SynchronizerFactory = _
    @BeanProperty
    var userListMerger: UserListMerger = _

    private var sessions = List[Session]()
    private var documents = List[DocumentActor]()

    override def login(username: String) = {
        if (username == null)
            throw new IllegalArgumentException("username cannot be null")

        if (sessions.exists {s => s.username == username})
            throw new UsernameAlreadyExistsException("username already logged in")

        // create the new session
        val newSession = new BasicSession(username, this)

        // notify the login
        sessions.foreach {session => session notifyUpdate NewUserLoggedIn(username)}

        // add the new session
        sessions = newSession :: sessions

        trace("new session")
        newSession
    }

    override def chat(from: Session, message: String) {
        if (from == null)
            throw new IllegalArgumentException("session cannot be null")
        if (message == null)
            throw new IllegalArgumentException("message cannot be null")
        if (!sessions.contains(from))
            throw new SessionNotExistsException("session not exists")

        sessions filter {_ != from} foreach {s => s notifyUpdate ChatMessage(from, message)}
    }

    override def newDocument(owner: Session, title: String, initialContent: String) = {
        if (documents exists {_.title == title})
            throw new DocumentTitleAlreadyExitsException("document title already exists")

        // create a document actor
        val newDocActor = newDocumentActor(title, initialContent)

        // add document to list
        documents = newDocActor :: documents

        // susbscribe owner to the document
        newDocActor ! Subscribe(owner)
    }

    override def deleteDocument(session: Session, title: String) {
        if (!sessions.contains(session))
            throw new SessionNotExistsException("session not exists")

        val doc = documentByTitle(title).getOrElse {
            throw new DocumentDeleteUnexistantException("document " + title + " does not exist")
        }

        // enviamos msg al documento
        doc ! Close(session)
    }

    protected def newDocumentActor(title: String, initialContent: String): DocumentActor = {
        // session factory
        val docSessionFactory = new BasicDocumentSessionFactory

        // document
        val doc = new BasicDocument(title, initialContent, docSessionFactory)

        // create a document actor
        val actor = new BasicDocumentActor(doc, newSyncFactory)
        // set the timeout
        actor.timeout = this.timeout

        // set the document actor to the session factory
        docSessionFactory.docActor = actor

        // start the actor
        actor.start

        actor
    }

    override def removeDeletedDocument(title: String) {
        documents = documents.filter {doc => doc.title != title}
    }

    override def documentList = documents.map {_.title}

    override def subscribe(session: Session, title: String) = {
        val doc = documentByTitle(title).getOrElse {
            warn("title does not exists")
            throw new DocumentTitleNotExistsException("no existe el documento")
        }
        doc ! Subscribe(session)
    }

    def logout(session: Session) = {
        if (!sessions.contains(session))
            throw new IllegalArgumentException("session not logged in")

        sessions = sessions filter {_ != session}
        documents foreach {doc => doc ! SilentUnsubscribe(session)}

        // notify the logout
        sessions.foreach {each => each notifyUpdate UserLoggedOut(session.username)}
    }

    override def userList(session: Session) =
        userListMerger.notifyUserList(session, sessions, documents)

    override def terminate =
        documents.foreach {doc => doc ! TerminateDocument()}

    def documentCount = documents size

    def sessionCount = sessions size

    def documentByTitle(docTitle: String) = documents find {_.title == docTitle}

    def documentSubscriberCount(docTitle: String) = {
        documentByTitle(docTitle).map(docActor => {
            trace("Sending Subscriber Count Request")

            docActor !! (SubscriberCount(), {
                case count: Int => count
            })
        })
    }

    protected def newSyncFactory: SynchronizerFactory = sync
}
