package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.DocumentListUpdate;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.UserListUpdate;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IDocumentListCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IUserListCallback;

public class RemoteDocumentsAdapter implements Documents {

	private IUserListCallback documentListCallback;
	private IDocumentListCallback userListCallback;

	@Override
	public synchronized void process(CommandFromKernel command) {
		if (command instanceof UserListUpdate) {
			Map<String, List<String>> usernames = ((UserListUpdate) command).usernames();

			if (documentListCallback != null)
				documentListCallback.onUserListResponse(usernames);
		}
		if (command instanceof DocumentListUpdate) {
			List<String> docs = ((DocumentListUpdate) command).docs();

			if (userListCallback != null)
				userListCallback.onDocumentListResponse(docs);
		}
	}

	public synchronized void installUserListCallback(IUserListCallback callback) {
		this.documentListCallback = callback;
	}

	public synchronized void installDocumentListCallback(IDocumentListCallback callback) {
		this.userListCallback = callback;
	}
}