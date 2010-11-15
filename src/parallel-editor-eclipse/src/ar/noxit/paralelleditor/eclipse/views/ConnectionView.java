package ar.noxit.paralelleditor.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralelleditor.eclipse.model.IModel;
import ar.noxit.paralelleditor.eclipse.model.Model;

public class ConnectionView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		List<ConnectionInfo> connections = new ArrayList<ConnectionInfo>();
		IModel<ConnectionInfo> selectedConnection = new Model<ConnectionInfo>();

		new HostsList(parent, SWT.NONE, Model.of(connections), selectedConnection);
		new ServerPanel(parent, SWT.NONE, selectedConnection);

		parent.pack();
	}

	@Override
	public void setFocus() {
	}
}
