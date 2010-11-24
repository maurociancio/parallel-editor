package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public interface ISession {

	public static interface IUserListCallback {
		void onUserListResponse(Map<String, List<String>> usernames);
	}

	public static interface IDocumentListCallback {
		void onDocumentListResponse(List<String> docs);
	}

	public static interface ISubscriptionCallback {
		void onSubscriptionResponse(String docTitle,
				String initialContent,
				IDocumentSession documentSession);
	}

	public static interface IChatCallback {
		void onNewChat(ConnectionInfo from,
				String username,
				String message);
	}

	public static interface IOnLoginFailureCallback {
		void onLoginFailure();
	}

	void chat(String message);

	void installUserListCallback(IUserListCallback callback);

	void requestUserList();

	void installDocumentListCallback(IDocumentListCallback callback);

	void requestDocumentList();

	void subscribe(String docTitle);

	void installSubscriptionResponseCallback(ISubscriptionCallback callback);

	void installChatCallback(IChatCallback chatCallback);

	void installOnLoginFailureCallback(IOnLoginFailureCallback callback);

	void close();
}
