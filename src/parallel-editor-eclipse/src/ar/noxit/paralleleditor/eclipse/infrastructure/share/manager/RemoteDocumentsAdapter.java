package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.UserListUpdate;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IUserListCallback;

public final class RemoteDocumentsAdapter implements Documents {

	private IUserListCallback documentListCallback;

	@Override
	public synchronized void process(CommandFromKernel command) {
		if (command instanceof UserListUpdate) {
			Map<String, List<String>> usernames = ((UserListUpdate) command).usernames();

			if (documentListCallback != null)
				documentListCallback.onUserListResponse(usernames);
		}
	}

	public synchronized void installUserListCallback(IUserListCallback callback) {
		this.documentListCallback = callback;
	}
}