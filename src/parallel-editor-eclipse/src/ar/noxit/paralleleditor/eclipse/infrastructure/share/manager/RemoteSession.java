package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralelleditor.eclipse.views.ConnectionInfo;
import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.common.messages.RemoteUserListRequest;

public class RemoteSession implements ISession {

	private final ConnectionInfo info;
	private final JSession newSession;
	private final RemoteDocumentsAdapter adapter;

	public RemoteSession(ConnectionInfo info, JSession newSession, RemoteDocumentsAdapter adapter) {
		Assert.isNotNull(info);
		Assert.isNotNull(newSession);
		Assert.isNotNull(adapter);

		this.info = info;
		this.newSession = newSession;
		this.adapter = adapter;
	}

	@Override
	public void installUserListCallback(IUserListCallback callback) {
		adapter.installUserListCallback(callback);
	}

	@Override
	public void requestDocumentList() {
		newSession.send(new RemoteUserListRequest());
	}
}