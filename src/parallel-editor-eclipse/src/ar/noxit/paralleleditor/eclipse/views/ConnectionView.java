package ar.noxit.paralleleditor.eclipse.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralleleditor.eclipse.Activator;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.ShareDocumentIntent;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;
import ar.noxit.paralleleditor.eclipse.model.IModel;
import ar.noxit.paralleleditor.eclipse.model.Model;

public class ConnectionView extends ViewPart {

	private final ShareManager shareManager = Activator.shareManager;
	private final IRemoteDocumentShare remoteDocShare = new ShareDocumentIntent(shareManager);
	private final IModel<List<ConnectionInfo>> hosts = Activator.hostsModel;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, true));

		IModel<ConnectionInfo> selectedConnection = new Model<ConnectionInfo>();

		// host list
		HostsList hostList = new HostsList(parent, SWT.NONE, hosts, selectedConnection);
		GridData hostListData = new GridData();
		hostListData.grabExcessVerticalSpace = true;
		hostListData.verticalAlignment = SWT.FILL;
		hostListData.grabExcessHorizontalSpace = true;
		hostListData.horizontalAlignment = SWT.FILL;
		hostListData.horizontalSpan = 1;
		hostList.setLayoutData(hostListData);

		ServerPanel serverPanel = new ServerPanel(parent, SWT.BORDER_SOLID, selectedConnection, shareManager,
				remoteDocShare);
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

	@Override
	public void dispose() {
		super.dispose();
		hosts.clearListeners();
	}
}
