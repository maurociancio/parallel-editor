package ar.noxit.paralelleditor.eclipse.views;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

abstract public class EditorOpener {

	public static void openNewEditor(String title, String content) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();

		if (page != null) {
			try {
				IDE.openEditor(page, new StringEditorInput(title, content),
						IDE.getEditorDescriptor(title).getId(), true);
			} catch (PartInitException e1) {
				throw new RuntimeException(e1);
			}
		}
	}

	private EditorOpener() {
	}
}
