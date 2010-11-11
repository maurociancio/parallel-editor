package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.texteditor.ITextEditor;

public final class ShareDocumentAction extends Action {

	private ITextEditorProvider textEditorProvider;
	private ITextFileBufferManager manager = ITextFileBufferManager.DEFAULT;

	public ShareDocumentAction(ITextEditorProvider textEditorProvider) {
		Assert.isNotNull(textEditorProvider);
		this.textEditorProvider = textEditorProvider;

		setText("Share this Doc");
	}

	@Override
	public void run() {
		ITextEditor textEditor = textEditorProvider.getTextEditor();
		System.out.println(textEditor);

		if (textEditor != null) {
			try {
				doRun(textEditor);
			} catch (CoreException e) {
			}
		}
	}

	/**
	 * Runs this action with a text editor as argument
	 * 
	 * @param textEditor
	 *            must not be null
	 * @throws CoreException
	 */
	protected void doRun(ITextEditor textEditor) throws CoreException {
		IFile file = (IFile) textEditor.getEditorInput().getAdapter(IFile.class);

		if (file != null) {
			IPath fullPath = file.getFullPath();
			LocationKind locationKind = LocationKind.IFILE;

			manager.connect(fullPath, locationKind, new NullProgressMonitor());
			IDocument document = manager.getTextFileBuffer(fullPath, locationKind).getDocument();

			document.addDocumentListener(new IDocumentListener() {

				@Override
				public void documentChanged(DocumentEvent event) {
					System.out.println("event fired " + event.fText + " " + event.fOffset + " " + event.fLength);
				}

				@Override
				public void documentAboutToBeChanged(DocumentEvent event) {
				}
			});
		} else {
			// TODO do something
		}
	}
}