package ar.noxit.paralleleditor.eclipse.menu.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import ar.noxit.paralleleditor.eclipse.Activator;

public class ConnectToServerAction extends Action {

	public ConnectToServerAction() {
		setText("Connect to Remote Collaboration Server");
	}

	@Override
	public void run() {
		try {
			getActivePage().showView(Activator.CONNECTIONVIEW);
		} catch (PartInitException e) {
			// TODO log here
		}
	}

	private static IWorkbenchPage getActivePage() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		return workbench.getActiveWorkbenchWindow().getActivePage();
	}
}