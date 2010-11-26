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
package ar.noxit.paralleleditor.kernel.basic.docsession

import ar.noxit.paralleleditor.kernel.{BasicDocumentSession, Session, Document}
import ar.noxit.paralleleditor.kernel.basic.{DocumentActor, DocumentSessionFactory}

class BasicDocumentSessionFactory extends DocumentSessionFactory {
    var docActor: DocumentActor = _

    def newDocumentSession(document: Document, who: Session) =
        new BasicDocumentSession(who, docActor)
}
