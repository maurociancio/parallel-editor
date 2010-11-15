package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocumentListener;

import ar.noxit.paralleleditor.common.operation.DocumentData;
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
		Assert.isNotNull(document);

		return getTextFileBuffer(document).getDocument().get();
	}

	@Override
	protected void installCallback(IDocument document, IDocumentListener listener) {
		Assert.isNotNull(document);
		Assert.isNotNull(listener);

		getTextFileBuffer(document).getDocument().addDocumentListener(listener);
	}

	protected ITextFileBuffer getTextFileBuffer(IDocument document) {
		Assert.isNotNull(document);

		LocationKind locationKind = document.getLocationKind();
		IPath fullPath = document.getFullPath();

		return get().getTextFileBuffer(fullPath, locationKind);
	}

	protected ITextFileBufferManager get() {
		return ITextFileBufferManager.DEFAULT;
	}

	@Override
	protected DocumentData getAdapterFor(final IDocument document) {
		// TODO adaptar mejor, sin reemplazar todo
		return new DocumentData() {

			private org.eclipse.jface.text.IDocument adapted = getTextFileBuffer(document).getDocument();

			@Override
			public void data_$eq(String data) {
				adapted.set(data);
			}

			@Override
			public String data() {
				return adapted.get();
			}
		};
	}
}
