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
package ar.noxit.paralleleditor.eclipse.editor.listeners;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;

public class TextEditorClosedListener implements IPartListener2 {

	private final ITextEditor textEditor;
	private final IPartService partService;
	private final IDocumentSession docSession;

	public TextEditorClosedListener(IPartService partService, ITextEditor textEditor, IDocumentSession docSession) {
		this.textEditor = textEditor;
		this.partService = partService;
		this.docSession = docSession;
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);

		if (part.equals(textEditor)) {
			docSession.unsubscribe();
			partService.removePartListener(this);
		}
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// nothing to do
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		// nothing to do
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// nothing to do
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// nothing to do
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// nothing to do
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// nothing to do
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// nothing to do
	}
}