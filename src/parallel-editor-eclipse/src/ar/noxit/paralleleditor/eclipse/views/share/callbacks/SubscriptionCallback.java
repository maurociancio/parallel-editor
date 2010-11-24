package ar.noxit.paralleleditor.eclipse.views.share.callbacks;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;

import ar.noxit.paralleleditor.eclipse.share.ISession.ISubscriptionCallback;
import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.views.share.IRemoteDocumentShare;

public class SubscriptionCallback implements ISubscriptionCallback {

	private final IRemoteDocumentShare remoteDocumentShare;

	public SubscriptionCallback(IRemoteDocumentShare remoteDocumentShare) {
		Assert.isNotNull(remoteDocumentShare);
		this.remoteDocumentShare = remoteDocumentShare;
	}

	@Override
	public void onSubscriptionResponse(final String docTitle,
			final String initialContent,
			final IDocumentSession docSession) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				remoteDocumentShare.shareRemoteDocument(docTitle, initialContent, docSession);
			}
		});
	}
}