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
package ar.noxit.paralleleditor.gui.sync

import ar.noxit.paralleleditor.gui.Synchronizer
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.{EditOperationJupiterSynchronizer, Message}

class SynchronizerAdapter(private val sync: EditOperationJupiterSynchronizer) extends Synchronizer {
    override def generate(op: EditOperation, send: (Message[EditOperation]) => Unit) =
        sync.generate(op, send)

    override def receive(message: Message[EditOperation], apply: (EditOperation) => Unit) =
        sync.receive(message, apply)
}
