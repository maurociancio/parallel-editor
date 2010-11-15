package ar.noxit.paralelleditor.eclipse.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class HostsList extends Composite {

	public HostsList(Composite parent, int style, IModel<java.util.List<Host>> hostsModel) {
		super(parent, style);

		// layout
		Shell shell = parent.getShell();
		setLayout(new FillLayout(SWT.VERTICAL));

		// hosts
		Label host = new Label(this, style);
		host.setText("Available hosts");

		// hosts list
		List hosts = new List(this, SWT.BORDER | SWT.V_SCROLL);

		// items
		java.util.List<Host> list = hostsModel.get();
		String arrayItems[] = new String[list.size()];
		Integer i = 0;
		for (Host h : list) {
			arrayItems[i] = (h.getHostname() + ":" + h.getPort());
			i = i + 1;
		}

		hosts.setItems(arrayItems);
	}
}
