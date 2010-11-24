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