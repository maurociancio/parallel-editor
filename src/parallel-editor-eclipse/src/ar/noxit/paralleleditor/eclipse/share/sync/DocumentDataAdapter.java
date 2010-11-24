package ar.noxit.paralleleditor.eclipse.share.sync;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.common.operation.Caret;
import ar.noxit.paralleleditor.common.operation.DocumentData;

public class DocumentDataAdapter implements DocumentData {

	private final org.eclipse.jface.text.IDocument adapted;
	private final StyledText adapter;

	public DocumentDataAdapter(org.eclipse.jface.text.IDocument eclipseDoc, ITextEditor textEditor) {
		Assert.isNotNull(eclipseDoc);
		Assert.isNotNull(textEditor);

		this.adapted = eclipseDoc;
		this.adapter = (StyledText) textEditor.getAdapter(Control.class);
	}

	@Override
	public String data() {
		return adapted.get();
	}

	@Override
	public void replace(int offset, int length, String newText) {
		try {
			adapted.replace(offset, length, newText);
		} catch (BadLocationException e) {
			// TODO log here
		}
	}

	@Override
	public Caret caret() {
		return new CaretAdapter();
	}

	private class CaretAdapter implements Caret {

		private final int x;
		private final int y;
		private int caretOffset;

		public CaretAdapter() {
			Point selection = adapter.getSelection();

			this.x = selection.x;
			this.y = selection.y;
			this.caretOffset = adapter.getCaretOffset();
		}

		@Override
		public int selectionLength() {
			return y - x;
		}

		@Override
		public int offset() {
			return x;
		}

		@Override
		public void change(int offset, int selectionLength) {
			if (x == caretOffset) {
				// left to right?
				adapter.setSelectionRange(offset + selectionLength, -selectionLength);
			} else {
				// right to left?
				adapter.setSelectionRange(offset, selectionLength);
			}
		}
	}

}
