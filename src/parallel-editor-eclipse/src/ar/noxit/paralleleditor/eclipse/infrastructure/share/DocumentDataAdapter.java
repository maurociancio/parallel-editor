package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.Caret;
import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;

public class DocumentDataAdapter implements DocumentData {

	private org.eclipse.jface.text.IDocument adapted;
	private ITextEditor textEditor;

	public DocumentDataAdapter(org.eclipse.jface.text.IDocument eclipseDoc, IDocument document) {
		Assert.isNotNull(eclipseDoc);
		Assert.isNotNull(document);

		adapted = eclipseDoc;
		textEditor = document.getTextEditor();
	}

	@Override
	public void data_$eq(String data) {
		adapted.set(data);

		ISelection selection = textEditor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection t = (ITextSelection) selection;
		}

		textEditor.selectAndReveal(0, 1);
	}

	@Override
	public String data() {
		return adapted.get();
	}

	@Override
	public Caret caret() {
		return new Caret() {

			@Override
			public int selectionLength() {
				return 0;
			}

			@Override
			public int offset() {
				return 0;
			}

			@Override
			public void change(int offset, int selectionLength) {
			}
		};
	}
}