package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;

public class ShareDocumentIntent extends AbstractShareDocumentIntent {

	public ShareDocumentIntent(IShareManager shareManager) {
		super(shareManager);
	}

	@Override
	protected String getContentFor(IDocument document) {
		Assert.isNotNull(document);

		return getEclipseDocument(document).get();
	}

	@Override
	protected void installCallback(IDocument document, IDocumentListener listener) {
		Assert.isNotNull(document);
		Assert.isNotNull(listener);

		getEclipseDocument(document).addDocumentListener(listener);
	}

	protected org.eclipse.jface.text.IDocument getEclipseDocument(IDocument document) {
		ITextEditor textEditor = document.getTextEditor();
		return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	}

	@Override
	protected DocumentData getAdapterFor(final IDocument document) {
		return new DocumentDataAdapter(getEclipseDocument(document), document.getTextEditor());
	}
}
