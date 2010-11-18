package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.DocumentData;

public class ShareDocumentIntent extends AbstractShareDocumentIntent {

	public ShareDocumentIntent(IShareManager shareManager) {
		super(shareManager);
	}

	@Override
	protected String getContentFor(ITextEditor textEditor) {
		Assert.isNotNull(textEditor);

		return getEclipseDocument(textEditor).get();
	}

	@Override
	protected void installCallback(ITextEditor document, IDocumentListener listener) {
		Assert.isNotNull(document);
		Assert.isNotNull(listener);

		getEclipseDocument(document).addDocumentListener(listener);
	}

	@Override
	protected DocumentData getAdapterFor(ITextEditor textEditor) {
		return new DocumentDataAdapter(getEclipseDocument(textEditor), textEditor);
	}

	private org.eclipse.jface.text.IDocument getEclipseDocument(ITextEditor textEditor) {
		return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	}
}
