package ar.noxit.paralleleditor.eclipse.infrastructure.texteditor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

import ar.noxit.paralleleditor.eclipse.menu.actions.ITextEditorProvider;

public class TextEditorProvider implements ITextEditorProvider {

	@Override
	public ITextEditor getTextEditor() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			return null;
		final IWorkbenchWindow ww = workbench.getActiveWorkbenchWindow();
		if (ww == null)
			return null;
		final IWorkbenchPage wp = ww.getActivePage();
		if (wp == null)
			return null;
		final IEditorPart ep = wp.getActiveEditor();
		if (ep instanceof ITextEditor)
			return (ITextEditor) ep;
		if (ep != null)
			return (ITextEditor) ep.getAdapter(ITextEditor.class);
		return null;
	}
}
