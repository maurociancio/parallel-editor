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

import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.converter.RemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.messages.RemoteUnsubscribeRequest;
import ar.noxit.paralleleditor.common.operation.DocumentOperation;
import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.share.sync.IRemoteMessageCallback;

public class DocumentSession implements IDocumentSession {

	private final JSession session;
	private final String docTitle;
	private final RemoteDocumentOperationConverter converter;

	public DocumentSession(String docTitle, JSession localSession, RemoteDocumentOperationConverter converter) {
		this.docTitle = docTitle;
		this.converter = converter;
		this.session = localSession;
	}

	@Override
	public void onNewLocalMessage(Message<EditOperation> message) {
		session.send(converter.convert(new DocumentOperation(docTitle, message)));
	}

	@Override
	public void unsubscribe() {
		session.send(new RemoteUnsubscribeRequest(docTitle));
	}

	@Override
	public void installCallback(IRemoteMessageCallback remoteCallback) {
	}
}