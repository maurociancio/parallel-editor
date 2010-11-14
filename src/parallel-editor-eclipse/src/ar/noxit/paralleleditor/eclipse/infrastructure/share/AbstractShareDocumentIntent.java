package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

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

			shareManager.createShare(fullPath.toString(), getContentFor(document));
		} catch (CoreException e) {
			onException(e);
		}

		// IDocument document =
		// textFileBufferManager.getTextFileBuffer(fullPath,
		// locationKind).getDocument();
		//
		// document.addDocumentListener(new IDocumentListener() {
		//
		// @Override
		// public void documentChanged(DocumentEvent event) {
		// System.out.println("event fired " + event.fText + " " + event.fOffset
		// + " " + event.fLength);
		// }
		//
		// @Override
		// public void documentAboutToBeChanged(DocumentEvent event) {
		// }
		// });
	}

	protected abstract String getContentFor(IDocument document);

	protected abstract ITextFileManager getTextFileBufferManager();

	protected void onException(CoreException e) {
		// TODO log
	}
}
