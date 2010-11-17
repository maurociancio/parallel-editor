package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;

public interface ISession {

	public static interface IUserListCallback {
		void onUserListResponse(Map<String, List<String>> usernames);
	}

	void installUserListCallback(IUserListCallback callback);

	void requestDocumentList();
}
