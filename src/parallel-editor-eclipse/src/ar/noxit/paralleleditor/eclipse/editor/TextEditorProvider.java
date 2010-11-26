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

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.menu.ITextEditorProvider;

public class TextEditorProvider implements ITextEditorProvider {

	@Override
	public ITextEditor getCurrentTextEditor() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return null;
		final IWorkbenchWindow ww = workbench.getActiveWorkbenchWindow();
		if (ww == null)
			return null;
		final IWorkbenchPage wp = ww.getActivePage();
		if (wp == null)
			return null;
		final IEditorPart ep = wp.getActiveEditor();
		if (ep instanceof ITextEditor)
			return (ITextEditor) ep;
		if (ep != null)
			return (ITextEditor) ep.getAdapter(ITextEditor.class);
		return null;
	}
}
