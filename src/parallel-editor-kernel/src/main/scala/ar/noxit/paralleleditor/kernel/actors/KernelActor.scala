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
package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.messages._
import ar.noxit.paralleleditor.common.logger.Loggable
import exceptions.{DocumentTitleNotExistsException, DocumentDeleteUnexistantException, DocumentTitleAlreadyExitsException, UsernameAlreadyExistsException}
import ar.noxit.paralleleditor.common.BaseActor

class KernelActor(val kernel: Kernel) extends BaseActor with Loggable {
    def act = {
        info("Started")

        loop {
            trace("Choosing")
            react {
                case LoginRequest(username) => {
                    trace("Login Requested by=[%s]", username)

                    try {
                        val newSession = kernel.login(username)

                        trace("Sending Login Response")
                        sender ! LoginResponse(newSession)
                    }
                    catch {
                        case e: UsernameAlreadyExistsException =>
                            sender ! UsernameAlreadyExists()
                    }
                }

                case DocumentListRequest(session) => {
                    trace("Document List Requested")

                    // ask kernel for the document list
                    val documentList = kernel.documentList

                    // return it to the caller
                    session notifyUpdate DocumentListResponse(documentList)
                }
                case SubscribeToDocumentRequest(session, title) => {
                    trace("subscribe to document")

                    try
                        kernel.subscribe(session, title)
                    catch {
                        case e: DocumentTitleNotExistsException =>
                            sender ! DocumentNotExists(title)
                    }
                }

                case NewDocumentRequest(session, title, initialContent) => {
                    trace("New Document Requested [%s]", title)

                    try
                        kernel.newDocument(session, title, initialContent)
                    catch {
                        case e: DocumentTitleAlreadyExitsException =>
                            sender ! DocumentTitleExists(title)
                    }
                }

                case UserListRequest(session) => {
                    trace("UserListRequest")
                    kernel.userList(session)
                }

                case ChatMessage(session, message) => {
                    trace("chat message")
                    kernel.chat(session, message)
                }

                case CloseDocument(session, docTitle) => {
                    trace("delete document")
                    try
                        kernel.deleteDocument(session, docTitle)
                    catch {
                        case e: DocumentDeleteUnexistantException =>
                            sender ! DocumentDeletionTitleNotExists(docTitle)
                    }
                }

                case DocumentDeleted(title) => {
                    trace("document deleted")
                    kernel.removeDeletedDocument(title)
                }

                case TerminateKernel() => {
                    trace("terminate kernel received")
                    kernel.terminate
                    doExit
                }

                case msg: Any => {
                    warn("Unknown message received [%s]", msg)
                }
            }
        }
        // la ejecución nunca llega acá
    }

    protected def doExit = {
        trace("exiting")
        exit
    }
}
