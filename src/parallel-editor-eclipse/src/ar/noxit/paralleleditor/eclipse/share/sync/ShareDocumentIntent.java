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
