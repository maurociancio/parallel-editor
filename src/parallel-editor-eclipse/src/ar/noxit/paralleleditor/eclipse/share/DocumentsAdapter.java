package ar.noxit.paralleleditor.eclipse.share;

import java.util.Date;
import java.util.HashMap;

import org.eclipse.core.runtime.Assert;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.client.ChatMessage;
import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.DocumentListUpdate;
import ar.noxit.paralleleditor.client.DocumentSubscription;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.client.NewSubscriberToDocument;
import ar.noxit.paralleleditor.client.NewUserLoggedIn;
import ar.noxit.paralleleditor.client.ProcessOperation;
import ar.noxit.paralleleditor.client.SubscriberLeftDocument;
import ar.noxit.paralleleditor.client.SubscriptionCancelled;
import ar.noxit.paralleleditor.client.UserListUpdate;
import ar.noxit.paralleleditor.client.UserLoggedOut;
import ar.noxit.paralleleditor.client.UsernameTaken;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.converter.RemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.share.ISession.IDocumentListCallback;
import ar.noxit.paralleleditor.eclipse.share.ISession.IOnLoginFailureCallback;
import ar.noxit.paralleleditor.eclipse.share.ISession.ISubscriptionCallback;
import ar.noxit.paralleleditor.eclipse.share.ISession.IUserListCallback;
import ar.noxit.paralleleditor.eclipse.share.sync.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.share.sync.RemoteMessageCallbackAdapter;

public class DocumentsAdapter implements Documents {

	private IUserListCallback documentListCallback;
	private IDocumentListCallback userListCallback;
	private ISubscriptionCallback subscriptionResponseCallback;
	private IChatCallback chatCallback;
	private LoginFailureCallback onLoginFailure;

	private JSession session;

	// connection info
	private final ConnectionInfo info;

	private final java.util.Map<String, RemoteMessageCallbackAdapter> callbacks = new HashMap<String, RemoteMessageCallbackAdapter>();

	// converter
	private final RemoteDocumentOperationConverter converter;

	public DocumentsAdapter(RemoteDocumentOperationConverter converter, ConnectionInfo info) {
		Assert.isNotNull(converter);
		Assert.isNotNull(info);

		this.converter = converter;
		this.info = info;
	}

	@Override
	public synchronized void process(CommandFromKernel command) {
		// user list update
		if (command instanceof UserListUpdate) {
			Map<String, List<String>> usernames = ((UserListUpdate) command).usernames();

			if (documentListCallback != null)
				documentListCallback.onUserListResponse(usernames);
		}

		// doc list update
		if (command instanceof DocumentListUpdate) {
			List<String> docs = ((DocumentListUpdate) command).docs();

			if (userListCallback != null)
				userListCallback.onDocumentListResponse(docs);
		}

		// chat
		if (command instanceof ChatMessage) {
			ChatMessage chatMessage = (ChatMessage) command;

			if (chatCallback != null)
				chatCallback.onNewChat(new Date(), info, chatMessage.username(), chatMessage.message());
		}

		// new subscriber
		if (command instanceof NewSubscriberToDocument) {
			final NewSubscriberToDocument subs = (NewSubscriberToDocument) command;

			if (chatCallback != null)
				chatCallback.onNewSubscriber(new Date(), subs.username(), subs.docTitle());
		}

		// subscriber left
		if (command instanceof SubscriberLeftDocument) {
			final SubscriberLeftDocument subs = (SubscriberLeftDocument) command;

			if (chatCallback != null)
				chatCallback.onSubscriberLeft(new Date(), subs.username(), subs.docTitle());
		}

		// new login
		if (command instanceof NewUserLoggedIn) {
			final String username = ((NewUserLoggedIn) command).username();

			if (chatCallback != null)
				chatCallback.onNewLogin(new Date(), username);
		}

		// new log out
		if (command instanceof UserLoggedOut) {
			final String username = ((UserLoggedOut) command).username();

			if (chatCallback != null)
				chatCallback.onNewLogout(new Date(), username);
		}

		// login error
		if (command instanceof UsernameTaken) {
			if (this.onLoginFailure != null)
				this.onLoginFailure.onLoginFailure();
		}

		// subscription
		if (command instanceof DocumentSubscription) {
			DocumentSubscription documentSubscription = (DocumentSubscription) command;

			final String docTitle = documentSubscription.title();
			final String initialContent = documentSubscription.initialContent();

			// the session could be set already
			if (!callbacks.containsKey(docTitle)) {
				final RemoteMessageCallbackAdapter adaptedCallback = new RemoteMessageCallbackAdapter();
				callbacks.put(docTitle, adaptedCallback);
			}

			DocumentSession docSession = new DocumentSession(docTitle, session, converter) {

				@Override
				public void installCallback(IRemoteMessageCallback newCallback) {
					DocumentsAdapter.this.installCallback(docTitle, newCallback);
				}
			};

			if (subscriptionResponseCallback != null)
				subscriptionResponseCallback.onSubscriptionResponse(docTitle, initialContent, docSession);
		}

		// subscription canceled
		if (command instanceof SubscriptionCancelled) {
			SubscriptionCancelled canceled = (SubscriptionCancelled) command;
			callbacks.remove(canceled.docTitle());
		}

		// operations
		if (command instanceof ProcessOperation) {
			ProcessOperation processOperation = (ProcessOperation) command;

			String docTitle = processOperation.title();
			Message<EditOperation> msg = processOperation.msg();

			RemoteMessageCallbackAdapter callback = callbacks.get(docTitle);
			if (callback != null)
				callback.onNewRemoteMessage(msg);
		}
	}

	public synchronized boolean isSubscribedTo(String docTitle) {
		Assert.isNotNull(docTitle);

		return callbacks.get(docTitle) != null;
	}

	public synchronized void verifyDocTitleNotExists(String docTitle) {
		Assert.isNotNull(docTitle);
		if (isSubscribedTo(docTitle)) {
			throw new DocumentAlreadySharedException("document already shared", docTitle);
		}
	}

	public synchronized void installUserListCallback(IUserListCallback callback) {
		this.documentListCallback = callback;
	}

	public synchronized void installDocumentListCallback(IDocumentListCallback callback) {
		this.userListCallback = callback;
	}

	public synchronized void installSubscriptionResponseCallback(ISubscriptionCallback callback) {
		this.subscriptionResponseCallback = callback;
	}

	public synchronized void setSession(JSession newSession) {
		this.session = newSession;
	}

	public synchronized void installChatCallback(IChatCallback chatCallback) {
		this.chatCallback = chatCallback;
	}

	public synchronized void installOnLoginFailureCallback(LoginFailureCallback callback) {
		this.onLoginFailure = callback;
	}

	public synchronized void adaptOnLoginFailureCallback(IOnLoginFailureCallback callback) {
		if (this.onLoginFailure != null)
			this.onLoginFailure.setCallback(callback);
	}

	public synchronized void installCallback(String docTitle, IRemoteMessageCallback remoteMessageCallback) {
		RemoteMessageCallbackAdapter adapter = callbacks.get(docTitle);
		if (adapter == null)
			adapter = new RemoteMessageCallbackAdapter();

		adapter.setAdapted(remoteMessageCallback);
		callbacks.put(docTitle, adapter);
	}

	public synchronized void removeCallback(String docTitle) {
		callbacks.remove(docTitle);
	}

	public synchronized void dispose() {
		subscriptionResponseCallback = null;
		userListCallback = null;
		documentListCallback = null;
		callbacks.clear();
	}

}