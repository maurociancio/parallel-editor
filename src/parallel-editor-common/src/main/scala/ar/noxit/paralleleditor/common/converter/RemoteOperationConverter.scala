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
package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{RemoteNullOpText, RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{NullOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation
}

class DefaultRemoteOperationConverter extends RemoteOperationConverter {
    def convert(o: RemoteOperation) = {
        o match {
            case at: RemoteAddText => new AddTextOperation(at.text, at.startPos, at.pword)
            case dt: RemoteDeleteText => new DeleteTextOperation(dt.startPos, dt.size)
            case n: RemoteNullOpText => new NullOperation
        }
    }
}
