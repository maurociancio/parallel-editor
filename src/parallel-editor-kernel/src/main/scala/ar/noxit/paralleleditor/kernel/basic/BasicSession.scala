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

import ar.noxit.paralleleditor.kernel.UpdateCallback
import ar.noxit.paralleleditor.kernel.Session

class BasicSession(val username: String, private val kernel: BasicKernel) extends Session {
    private var updateCallback: UpdateCallback = _

    if (username == null)
        throw new IllegalArgumentException("username cannot be null")

    override def installOnUpdateCallback(callback: UpdateCallback) = this updateCallback = callback

    override def notifyUpdate(message: Any) = {
        if (updateCallback != null)
            updateCallback update message
    }

    //TODO comparar tambien el kernel
    override def equals(obj: Any) = {
        obj.isInstanceOf[Session] && obj.asInstanceOf[Session].username == username
    }

    override def logout = kernel logout this
}
