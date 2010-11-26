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
package ar.noxit.paralleleditor.eclipse;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;

import org.easymock.EasyMock;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.eclipse.menu.Document;
import ar.noxit.paralleleditor.eclipse.share.sync.AbstractShareDocumentIntent;
import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.share.sync.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.share.sync.ITextEditorDisabler;
import ar.noxit.paralleleditor.eclipse.views.share.IShareManager;

@Test
public class ShareDocumentIntentTest {

	private int countListener;

	@BeforeMethod
	public void before() {
		countListener = 0;
	}

	@Test
	public void testShareDocument() throws Exception {
		Path path = new Path("/");

		IDocumentSession docSession = EasyMock.createMock(IDocumentSession.class);
		EasyMock.replay(docSession);

		IShareManager shareManager = EasyMock.createMock(IShareManager.class);
		EasyMock.expect(shareManager.createLocalShare(
				EasyMock.eq("/"), EasyMock.eq("initial"), (IRemoteMessageCallback) EasyMock.anyObject())).
						andReturn(docSession);
		EasyMock.replay(shareManager);

		final DocumentData data = EasyMock.createMock(DocumentData.class);
		EasyMock.replay(data);

		final ITextEditorDisabler enabler = EasyMock.createMock(ITextEditorDisabler.class);
		EasyMock.replay(enabler);

		AbstractShareDocumentIntent shareDocumentIntent = new AbstractShareDocumentIntent(shareManager) {

			@Override
			protected String getContentFor(ITextEditor textEditor) {
				return "initial";
			}

			@Override
			protected void installCallback(ITextEditor textEditor, IDocumentListener listener) {
				countListener = countListener + 1;
			}

			@Override
			protected DocumentData getAdapterFor(ITextEditor textEditor) {
				return data;
			}

			@Override
			protected ITextEditorDisabler adapt(ITextEditor textEditor) {
				return enabler;
			}

			@Override
			protected void installOnCloseTextEditorCallback(ITextEditor textEditor, IDocumentSession docSession) {
			}
		};

		ITextEditor textEditor = createMock(ITextEditor.class);
		replay(textEditor);

		shareDocumentIntent.shareDocument(new Document(path, LocationKind.IFILE, textEditor));
		EasyMock.verify(shareManager, docSession, data, enabler);
		Assert.assertEquals(1, countListener);
	}
}
