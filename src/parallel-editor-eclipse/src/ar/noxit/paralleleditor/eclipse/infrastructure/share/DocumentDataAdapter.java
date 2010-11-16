package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.Caret;
import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;

public class DocumentDataAdapter implements DocumentData {

	private final org.eclipse.jface.text.IDocument adapted;
	private final ITextEditor textEditor;
	private final StyledText adapter;

	public DocumentDataAdapter(org.eclipse.jface.text.IDocument eclipseDoc, IDocument document) {
		Assert.isNotNull(eclipseDoc);
		Assert.isNotNull(document);

		this.adapted = eclipseDoc;
		this.textEditor = document.getTextEditor();
		this.adapter = (StyledText) textEditor.getAdapter(Control.class);
	}

	@Override
	public void data_$eq(String data) {
		adapted.set(data);
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
				return adapter.getSelectionCount();
			}

			@Override
			public int offset() {
				return adapter.getCaretOffset();
			}

			@Override
			public void change(int offset, int selectionLength) {
				textEditor.selectAndReveal(offset, selectionLength);
			}
		};
	}
}
