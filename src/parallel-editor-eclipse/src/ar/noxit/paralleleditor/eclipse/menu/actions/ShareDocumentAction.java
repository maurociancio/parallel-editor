package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.texteditor.ITextEditor;

public final class ShareDocumentAction extends Action {

	private ITextEditorProvider textEditorProvider;

	public ShareDocumentAction(ITextEditorProvider textEditorProvider) {
		Assert.isNotNull(textEditorProvider);
		this.textEditorProvider = textEditorProvider;

		setText("Share this Doc");
	}

	@Override
	public void run() {
		ITextEditor textEditor = textEditorProvider.getTextEditor();

		System.out.println(textEditor);
	}
}