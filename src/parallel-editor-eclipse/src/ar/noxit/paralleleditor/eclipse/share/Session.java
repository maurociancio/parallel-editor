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
package ar.noxit.paralleleditor.eclipse.share;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.common.messages.RemoteDocumentListRequest;
import ar.noxit.paralleleditor.common.messages.RemoteSendChatMessage;
import ar.noxit.paralleleditor.common.messages.RemoteSubscribeRequest;
import ar.noxit.paralleleditor.common.messages.RemoteUserListRequest;

public class Session implements ISession {

	private final JSession session;
	private final DocumentsAdapter adapter;

	public Session(JSession newSession, DocumentsAdapter adapter) {
		Assert.isNotNull(newSession);
		Assert.isNotNull(adapter);

		this.session = newSession;
		this.adapter = adapter;
		this.adapter.setSession(newSession);
	}

	@Override
	public void installUserListCallback(IUserListCallback callback) {
		adapter.installUserListCallback(callback);
	}

	@Override
	public void requestUserList() {
		session.send(new RemoteUserListRequest());
	}

	@Override
	public void installDocumentListCallback(IDocumentListCallback callback) {
		adapter.installDocumentListCallback(callback);
	}

	@Override
	public void requestDocumentList() {
		session.send(new RemoteDocumentListRequest());
	}

	@Override
	public void chat(String message) {
		session.send(new RemoteSendChatMessage(message));
	}

	@Override
	public void subscribe(String docTitle) {
		if (adapter.isSubscribedTo(docTitle)) {
			throw new SubscriptionAlreadyExistsException("subscription already exists to this document", docTitle);
		}
		session.send(new RemoteSubscribeRequest(docTitle));
	}

	@Override
	public void installSubscriptionResponseCallback(ISubscriptionCallback callback) {
		adapter.installSubscriptionResponseCallback(callback);
	}

	@Override
	public void installChatCallback(IChatCallback chatCallback) {
		adapter.installChatCallback(chatCallback);
	}

	@Override
	public void installOnLoginFailureCallback(IOnLoginFailureCallback callback) {
		adapter.adaptOnLoginFailureCallback(callback);
	}

	@Override
	public void close() {
		session.close();
	}
}