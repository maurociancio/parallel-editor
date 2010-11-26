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
package ar.noxit.paralleleditor.eclipse.menu;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.share.DocumentAlreadySharedException;

public abstract class AbstractShareDocumentAction extends Action {

	private final ITextEditorProvider textEditorProvider;
	private final IShareLocalDocumentIntent shareDocumentIntent;

	public AbstractShareDocumentAction(ITextEditorProvider textEditorProvider, IShareLocalDocumentIntent shareDocIntent) {
		Assert.isNotNull(textEditorProvider);
		Assert.isNotNull(shareDocIntent);

		this.textEditorProvider = textEditorProvider;
		this.shareDocumentIntent = shareDocIntent;

		setText("Share this Document");
	}

	@Override
	public void run() {
		ITextEditor textEditor = textEditorProvider.getCurrentTextEditor();

		if (textEditor != null) {
			doRun(textEditor);
		} else {
			onNullTextEditor();
		}
	}

	/**
	 * Runs this action with a text editor as argument
	 * 
	 * @param textEditor
	 *            must not be null
	 */
	protected void doRun(ITextEditor textEditor) {
		IFile file = (IFile) textEditor.getEditorInput().getAdapter(IFile.class);

		if (file != null) {
			IPath fullPath = file.getFullPath();
			LocationKind locationKind = LocationKind.IFILE;

			try {
				shareDocumentIntent.shareDocument(new Document(fullPath, locationKind, textEditor));

				activateView();
			} catch (DocumentAlreadySharedException e) {
				// TODO log here the full stacktrace

				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"Document already shared",
						"This document \"" + e.getDocTitle() + "\" is already shared.");
			} catch (Exception e) {
				// TODO log here the full stacktrace

				// the kernel or the client we're not created, notify the user
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						"Error while creating the share",
						"The share could not be created. It is possible that the default port is taken. " +
								"Please see the logs in order to find more information."
						);
			}
		} else {
			onNullFile();
		}
	}

	protected void activateView() {
	}

	protected void onNullFile() {
	}

	protected void onNullTextEditor() {
	}
}