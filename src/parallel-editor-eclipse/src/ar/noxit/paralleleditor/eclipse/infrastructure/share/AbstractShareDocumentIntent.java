package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocumentListener;

import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.operation.EditOperation;
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
			ITextFileManager textFileBufferManager = getTextFileBufferManager();

			IPath fullPath = document.getFullPath();
			LocationKind locationKind = document.getLocationKind();

			textFileBufferManager.connect(fullPath, locationKind, new NullProgressMonitor());

			IDocumentSession docSession = shareManager.createShare(fullPath.toString(), getContentFor(document),
					new IOperationCallback() {

						@Override
						public void processOperation(Message<EditOperation> message) {
							System.out.println(message);
						}
					});

			installCallback(document, new EclipseDocumentListener());
		} catch (CoreException e) {
			onException(e);
		}
	}

	protected abstract void installCallback(IDocument document, IDocumentListener listener);

	protected abstract String getContentFor(IDocument document);

	protected abstract ITextFileManager getTextFileBufferManager();

	protected void onException(CoreException e) {
		// TODO log
	}
}
