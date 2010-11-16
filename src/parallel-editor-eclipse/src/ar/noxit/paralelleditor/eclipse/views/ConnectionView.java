package ar.noxit.paralelleditor.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralelleditor.eclipse.model.IModel;
import ar.noxit.paralelleditor.eclipse.model.Model;

public class ConnectionView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, true));

		List<ConnectionInfo> connections = new ArrayList<ConnectionInfo>();
		IModel<ConnectionInfo> selectedConnection = new Model<ConnectionInfo>();

		// host list
		HostsList hostList = new HostsList(parent, SWT.NONE,
				Model.of(connections), selectedConnection);
		GridData hostListData = new GridData();
		hostListData.grabExcessVerticalSpace = true;
		hostListData.verticalAlignment = SWT.FILL;
		hostListData.grabExcessHorizontalSpace = true;
		hostListData.horizontalAlignment = SWT.FILL;
		hostListData.horizontalSpan = 1;
		hostList.setLayoutData(hostListData);

		// server panel
		ServerPanel serverPanel = new ServerPanel(parent, SWT.BORDER_SOLID,
				selectedConnection);
		GridData serverPanelData = new GridData();
		serverPanelData.horizontalSpan = 2;
		serverPanelData.horizontalAlignment = SWT.FILL;
		serverPanelData.grabExcessHorizontalSpace = true;
		serverPanelData.verticalAlignment = SWT.FILL;
		serverPanelData.grabExcessVerticalSpace = true;
		serverPanel.setLayoutData(serverPanelData);

	}

	@Override
	public void setFocus() {
	}
}
