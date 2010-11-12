package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.texteditor.ITextEditor;

public class ShareDocumentAction extends Action {

	private ITextEditorProvider textEditorProvider;
	// private ITextFileBufferManager manager = ITextFileBufferManager.DEFAULT;

	private IShareDocumentIntent shareDocumentIntent;

	public ShareDocumentAction(ITextEditorProvider textEditorProvider, IShareDocumentIntent shareDocIntent) {
		Assert.isNotNull(textEditorProvider);
		Assert.isNotNull(shareDocIntent);

		this.textEditorProvider = textEditorProvider;
		this.shareDocumentIntent = shareDocIntent;

		setText("Share this Doc");
	}

	@Override
	public void run() {
		ITextEditor textEditor = textEditorProvider.getTextEditor();

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

			shareDocumentIntent.shareDocument(fullPath, locationKind);

			// manager.connect(fullPath, locationKind, new
			// NullProgressMonitor());
			// IDocument document = manager.getTextFileBuffer(fullPath,
			// locationKind).getDocument();

			// document.addDocumentListener(new IDocumentListener() {
			//
			// @Override
			// public void documentChanged(DocumentEvent event) {
			// System.out.println("event fired " + event.fText + " " +
			// event.fOffset + " " + event.fLength);
			// }
			//
			// @Override
			// public void documentAboutToBeChanged(DocumentEvent event) {
			// }
			// });
		} else {
			onNullFile();
		}
	}

	protected void onNullFile() {
	}

	protected void onNullTextEditor() {
	}
}