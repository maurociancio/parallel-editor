package ar.noxit.paralleleditor.eclipse;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ar.noxit.paralleleditor.eclipse.menu.actions.Document;
import ar.noxit.paralleleditor.eclipse.menu.actions.IShareDocumentIntent;
import ar.noxit.paralleleditor.eclipse.menu.actions.ITextEditorProvider;
import ar.noxit.paralleleditor.eclipse.menu.actions.ShareDocumentAction;

@Test
public class ShareDocumentActionTest {

	private int onNullTextEditorTimes;
	private int onNullFileTimes;

	@BeforeMethod
	public void before() {
		onNullTextEditorTimes = 0;
		onNullFileTimes = 0;
	}

	@Test
	public void testShareDocumentUnexistantTextEditor() {
		ITextEditorProvider textEditorProvider = createMock(ITextEditorProvider.class);
		expect(textEditorProvider.getCurrentTextEditor()).andReturn(null);
		replay(textEditorProvider);

		IShareDocumentIntent shareDocIntent = createMock(IShareDocumentIntent.class);
		replay(shareDocIntent);

		ShareDocumentAction shareDocumentAction = new ShareDocumentAction(textEditorProvider, shareDocIntent) {
			@Override
			protected void onNullTextEditor() {
				onNullTextEditorTimes = onNullTextEditorTimes + 1;
			}
		};
		shareDocumentAction.run();

		Assert.assertEquals(1, onNullTextEditorTimes);
		verify(shareDocIntent, textEditorProvider);
	}

	@Test
	public void testShareDocumentUnexistantFile() {
		IEditorInput editorInput = createMock(IEditorInput.class);
		expect(editorInput.getAdapter(IFile.class)).andReturn(null);
		replay(editorInput);

		ITextEditor textEditor = createMock(ITextEditor.class);
		expect(textEditor.getEditorInput()).andReturn(editorInput);
		replay(textEditor);

		ITextEditorProvider textEditorProvider = createMock(ITextEditorProvider.class);
		expect(textEditorProvider.getCurrentTextEditor()).andReturn(textEditor);
		replay(textEditorProvider);

		IShareDocumentIntent shareDocIntent = createMock(IShareDocumentIntent.class);
		replay(shareDocIntent);

		ShareDocumentAction shareDocumentAction = new ShareDocumentAction(textEditorProvider, shareDocIntent) {

			@Override
			protected void onNullFile() {
				onNullFileTimes = onNullFileTimes + 1;
			}
		};
		shareDocumentAction.run();

		Assert.assertEquals(1, onNullFileTimes);
		verify(shareDocIntent, textEditor, textEditorProvider, editorInput);
	}

	@Test
	public void testShareDocumentExistantFile() {
		IFile file = createMock(IFile.class);
		Path path = new Path("/");
		expect(file.getFullPath()).andReturn(path);
		replay(file);

		IEditorInput editorInput = createMock(IEditorInput.class);
		expect(editorInput.getAdapter(IFile.class)).andReturn(file);
		replay(editorInput);

		ITextEditor textEditor = createMock(ITextEditor.class);
		expect(textEditor.getEditorInput()).andReturn(editorInput);
		replay(textEditor);

		ITextEditorProvider textEditorProvider = createMock(ITextEditorProvider.class);
		expect(textEditorProvider.getCurrentTextEditor()).andReturn(textEditor);
		replay(textEditorProvider);

		IShareDocumentIntent shareDocIntent = createMock(IShareDocumentIntent.class);
		shareDocIntent.shareDocument(new Document(path, LocationKind.IFILE, textEditor));
		replay(shareDocIntent);

		ShareDocumentAction shareDocumentAction = new ShareDocumentAction(textEditorProvider, shareDocIntent);
		shareDocumentAction.run();

		verify(shareDocIntent, textEditor, textEditorProvider, editorInput, file);
	}
}
