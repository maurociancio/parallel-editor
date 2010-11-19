package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.common.messages.RemoteDocumentListRequest;
import ar.noxit.paralleleditor.common.messages.RemoteSubscribeRequest;
import ar.noxit.paralleleditor.common.messages.RemoteUserListRequest;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

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
		this.adapter.setRemoteSession(newSession);
	}

	@Override
	public void installUserListCallback(IUserListCallback callback) {
		adapter.installUserListCallback(callback);
	}

	@Override
	public void requestUserList() {
		newSession.send(new RemoteUserListRequest());
	}

	@Override
	public void installDocumentListCallback(IDocumentListCallback callback) {
		adapter.installDocumentListCallback(callback);
	}

	@Override
	public void requestDocumentList() {
		newSession.send(new RemoteDocumentListRequest());
	}

	@Override
	public void subscribe(String docTitle) {
		if (adapter.isSubscribedTo(docTitle)) {
			throw new SubscriptionAlreadyExistsException("subscription already exists to this document", docTitle);
		}
		newSession.send(new RemoteSubscribeRequest(docTitle));
	}

	@Override
	public void installSubscriptionResponseCallback(ISubscriptionCallback callback) {
		adapter.installSubscriptionResponseCallback(callback);
	}

	@Override
	public void close() {
		newSession.close();
	}
}