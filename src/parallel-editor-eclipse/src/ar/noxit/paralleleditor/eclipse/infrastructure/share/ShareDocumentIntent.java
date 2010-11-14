package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;

public class ShareDocumentIntent extends AbstractShareDocumentIntent {

	public ShareDocumentIntent(IShareManager shareManager) {
		super(shareManager);
	}

	protected ITextFileManager getTextFileBufferManager() {
		return new ITextFileManager() {

			@Override
			public void connect(IPath location, LocationKind locationKind, IProgressMonitor monitor)
					throws CoreException {
				get().connect(location, locationKind, monitor);
			}
		};
	}

	@Override
	protected String getContentFor(IDocument document) {
		LocationKind locationKind = document.getLocationKind();
		IPath fullPath = document.getFullPath();

		return get().getTextFileBuffer(fullPath, locationKind).getDocument().get();
	}

	protected ITextFileBufferManager get() {
		return ITextFileBufferManager.DEFAULT;
	}
}
