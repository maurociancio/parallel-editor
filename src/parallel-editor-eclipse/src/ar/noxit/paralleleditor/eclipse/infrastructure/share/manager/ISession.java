package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;

public interface ISession {

	public static interface IUserListCallback {
		void onUserListResponse(Map<String, List<String>> usernames);
	}

	public static interface IDocumentListCallback {
		void onDocumentListResponse(List<String> docs);
	}

	public static interface ISubscriptionCallback {
		void onDocumentListResponse(String docTitle,
				String initialContent,
				IDocumentSession documentSession);
	}

	void installUserListCallback(IUserListCallback callback);

	void requestUserList();

	void installDocumentListCallback(IDocumentListCallback callback);

	void requestDocumentList();

	void subscribe(String docTitle);

	void installSubscriptionResponseCallback(ISubscriptionCallback callback);
	
	void close();
}
