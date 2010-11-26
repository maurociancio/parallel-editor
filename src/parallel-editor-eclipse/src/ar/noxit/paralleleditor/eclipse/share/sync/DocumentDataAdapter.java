/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
