package ar.noxit.paralleleditor.eclipse.share.sync;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.editor.TextEditorDisabler;
import ar.noxit.paralleleditor.eclipse.editor.listeners.TextEditorClosedListener;
import ar.noxit.paralleleditor.eclipse.views.share.IShareManager;

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

	@Override
	protected ITextEditorDisabler adapt(ITextEditor textEditor) {
		return new TextEditorDisabler(textEditor);
	}

	@Override
	protected void installOnCloseTextEditorCallback(ITextEditor textEditor, IDocumentSession docSession) {
		IPartService partService = textEditor.getSite().getWorkbenchWindow().getPartService();
		partService.addPartListener(new TextEditorClosedListener(partService, textEditor, docSession));
	}
}
