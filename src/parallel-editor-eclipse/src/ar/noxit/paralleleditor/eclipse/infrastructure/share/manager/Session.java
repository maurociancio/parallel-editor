package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.common.messages.RemoteDocumentListRequest;
import ar.noxit.paralleleditor.common.messages.RemoteSendChatMessage;
import ar.noxit.paralleleditor.common.messages.RemoteSubscribeRequest;
import ar.noxit.paralleleditor.common.messages.RemoteUserListRequest;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public class Session implements ISession {

	private final ConnectionInfo info;
	private final JSession session;
	private final DocumentsAdapter adapter;

	public Session(ConnectionInfo info, JSession newSession, DocumentsAdapter adapter) {
		Assert.isNotNull(info);
		Assert.isNotNull(newSession);
		Assert.isNotNull(adapter);

		this.info = info;
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
	public void close() {
		session.close();
	}

}