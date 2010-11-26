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
package ar.noxit.paralleleditor.eclipse.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.share.sync.ITextEditorDisabler;

public class TextEditorDisabler implements ITextEditorDisabler {

	private final StyledText textEditor;

	public TextEditorDisabler(ITextEditor textEditor) {
		Assert.isNotNull(textEditor);
		this.textEditor = (StyledText) textEditor.getAdapter(Control.class);
	}

	@Override
	public void enableInput() {
		setEnable(true);
	}

	@Override
	public void disableInput() {
		setEnable(false);
	}

	private void setEnable(final boolean editable) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				textEditor.setRedraw(editable);
				textEditor.setEditable(editable);
			}
		});
	}
}
