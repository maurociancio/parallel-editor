package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import ar.noxit.paralleleditor.common.operation.Caret;
import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.IDocument;

public class DocumentDataAdapter implements DocumentData {

	private final org.eclipse.jface.text.IDocument adapted;
	private final StyledText adapter;

	public DocumentDataAdapter(org.eclipse.jface.text.IDocument eclipseDoc, IDocument document) {
		Assert.isNotNull(eclipseDoc);
		Assert.isNotNull(document);

		this.adapted = eclipseDoc;
		this.adapter = (StyledText) document.getTextEditor().getAdapter(Control.class);
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
