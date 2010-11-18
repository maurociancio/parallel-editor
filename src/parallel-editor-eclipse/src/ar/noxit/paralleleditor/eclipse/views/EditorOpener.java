package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

abstract public class EditorOpener {

	public static IEditorPart openNewEditor(String title, String content) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();

		if (page != null) {
			try {
				return IDE.openEditor(page, new StringEditorInput(title, content),
						IDE.getEditorDescriptor(title).getId(), true);
			} catch (PartInitException e1) {
				throw new RuntimeException(e1);
			}
		}
		return null;
	}

	private EditorOpener() {
	}
}
