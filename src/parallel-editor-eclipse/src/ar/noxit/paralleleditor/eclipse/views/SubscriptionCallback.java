package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.ISubscriptionCallback;

public class SubscriptionCallback implements ISubscriptionCallback {

	private IRemoteDocumentShare remoteDocumentShare;

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