package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import java.util.HashMap;

import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.DocumentListUpdate;
import ar.noxit.paralleleditor.client.DocumentSubscription;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.client.ProcessOperation;
import ar.noxit.paralleleditor.client.UserListUpdate;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.converter.DefaultEditOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultRemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultSyncOperationConverter;
import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.RemoteMessageCallbackAdapter;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IDocumentListCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.ISubscriptionCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IUserListCallback;

public class RemoteDocumentsAdapter implements Documents {

	private IUserListCallback documentListCallback;
	private IDocumentListCallback userListCallback;
	private ISubscriptionCallback subscriptionResponseCallback;
	private JSession remoteSession;

	private java.util.Map<String, RemoteMessageCallbackAdapter> callbacks = new HashMap<String, RemoteMessageCallbackAdapter>();

	// converter
	private DefaultRemoteDocumentOperationConverter converter = new DefaultRemoteDocumentOperationConverter(
			new DefaultSyncOperationConverter(new DefaultEditOperationConverter()));

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
		if (command instanceof DocumentSubscription) {
			DocumentSubscription documentSubscription = (DocumentSubscription) command;

			final String docTitle = documentSubscription.title();
			final String initialContent = documentSubscription.initialContent();

			final RemoteMessageCallbackAdapter adaptedCallback = new RemoteMessageCallbackAdapter();
			callbacks.put(docTitle, adaptedCallback);

			DocumentSession docSession = new DocumentSession(docTitle, remoteSession, converter) {

				@Override
				public void installCallback(IRemoteMessageCallback newCallback) {
					adaptedCallback.setAdapted(newCallback);
				}
			};

			if (subscriptionResponseCallback != null)
				subscriptionResponseCallback.onDocumentListResponse(docTitle, initialContent, docSession);
		}

		if (command instanceof ProcessOperation) {
			ProcessOperation processOperation = (ProcessOperation) command;

			String docTitle = processOperation.title();
			Message<EditOperation> msg = processOperation.msg();

			// TODO valir not null
			callbacks.get(docTitle).onNewRemoteMessage(msg);
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

	public synchronized void setRemoteSession(JSession newSession) {
		this.remoteSession = newSession;
	}
}