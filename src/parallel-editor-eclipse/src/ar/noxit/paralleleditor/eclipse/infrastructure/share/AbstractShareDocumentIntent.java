package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import ar.noxit.paralleleditor.eclipse.menu.actions.IShareDocumentIntent;

public abstract class AbstractShareDocumentIntent implements IShareDocumentIntent {

	@Override
	public void shareDocument(IPath fullPath, LocationKind locationKind) {
		Assert.isNotNull(fullPath);
		Assert.isNotNull(locationKind);

		try {
			ITextFileManager textFileBufferManager = getTextFileBufferManager();
			textFileBufferManager.connect(fullPath, locationKind, new NullProgressMonitor());
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

	protected abstract ITextFileManager getTextFileBufferManager();

	protected void onException(CoreException e) {
		// TODO log
	}
}
