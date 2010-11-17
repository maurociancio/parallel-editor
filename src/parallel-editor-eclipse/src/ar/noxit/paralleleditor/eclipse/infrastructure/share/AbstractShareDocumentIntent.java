package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocumentListener;

import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;
import ar.noxit.paralleleditor.eclipse.menu.actions.IShareDocumentIntent;

public abstract class AbstractShareDocumentIntent implements IShareDocumentIntent {

	private IShareManager shareManager;

	public AbstractShareDocumentIntent(IShareManager shareManager) {
		Assert.isNotNull(shareManager);

		this.shareManager = shareManager;
	}

	@Override
	public void shareDocument(IDocument document) {
		Assert.isNotNull(document);

		try {
			connectToBufferManager(document);
			String fullPath = document.getFullPath().toString();

			// callback from kernel
			RemoteMessageCallbackAdapter remoteCallback = new RemoteMessageCallbackAdapter();

			// create the new document session
			IDocumentSession docSession = shareManager.createLocalShare(fullPath, getContentFor(document), remoteCallback);

			// callback from editor
			Synchronizer sync = new Synchronizer(docSession, getAdapterFor(document));

			// adapt callbacks
			remoteCallback.setAdapted(sync);

			// install callback on editor
			installCallback(document, new EclipseDocumentListener(sync));
		} catch (CoreException e) {
			onException(e);
		}
	}

	protected void connectToBufferManager(IDocument document) throws CoreException {
		ITextFileManager textFileBufferManager = getTextFileBufferManager();

		IPath fullPath = document.getFullPath();
		LocationKind locationKind = document.getLocationKind();

		textFileBufferManager.connect(fullPath, locationKind, new NullProgressMonitor());
	}

	protected void onException(CoreException e) {
		// TODO log
	}

	protected abstract DocumentData getAdapterFor(IDocument document);

	protected abstract String getContentFor(IDocument document);

	protected abstract void installCallback(IDocument document, IDocumentListener listener);

	protected abstract ITextFileManager getTextFileBufferManager();
}
