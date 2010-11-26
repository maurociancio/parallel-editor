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
package ar.noxit.paralleleditor.eclipse.share.sync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public final class RemoteMessageCallbackAdapter implements IRemoteMessageCallback {

	private IRemoteMessageCallback adapted = null;
	private List<Message<EditOperation>> queued = new ArrayList<Message<EditOperation>>();

	@Override
	public synchronized void onNewRemoteMessage(Message<EditOperation> message) {
		if (adapted == null) {
			queued.add(message);
		} else {
			adapted.onNewRemoteMessage(message);
		}
	}

	public synchronized void setAdapted(IRemoteMessageCallback adapted) {
		Assert.isNotNull(adapted);

		if (this.adapted == null) {
			Iterator<Message<EditOperation>> it = queued.iterator();
			while (it.hasNext()) {
				adapted.onNewRemoteMessage(it.next());
				it.remove();
			}
		}

		this.adapted = adapted;
	}
}