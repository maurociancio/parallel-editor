package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ShareDocumentAction extends AbstractShareDocumentAction {

	public ShareDocumentAction(ITextEditorProvider textEditorProvider, IShareLocalDocumentIntent shareDocIntent) {
		super(textEditorProvider, shareDocIntent);
	}

	@Override
	protected void activateView() {
		try {
			getActivePage().showView("ar.noxit.paralleleditor.connectionview");
		} catch (PartInitException e) {
			// TODO log stack trace
		}
	}

	private static IWorkbenchPage getActivePage() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		return workbench.getActiveWorkbenchWindow().getActivePage();
	}
}
