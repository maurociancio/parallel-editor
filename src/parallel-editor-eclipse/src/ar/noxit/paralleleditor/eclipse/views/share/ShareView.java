/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.eclipse.views.share;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralleleditor.eclipse.Activator;
import ar.noxit.paralleleditor.eclipse.locator.IModel;
import ar.noxit.paralleleditor.eclipse.locator.Model;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.ShareManager;
import ar.noxit.paralleleditor.eclipse.share.sync.ShareDocumentIntent;

public class ShareView extends ViewPart {

	private final ShareManager shareManager = Activator.shareManager;
	private final IRemoteDocumentShare remoteDocShare = new ShareDocumentIntent(shareManager);
	private final IModel<List<ConnectionInfo>> hosts = Activator.hostsModel;
	private final IModel<ConnectionInfo> selectedConnection = new Model<ConnectionInfo>();

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(3, true));

		// host list
		HostsList hostList = new HostsList(parent, SWT.NONE, hosts, selectedConnection, shareManager);
		GridData hostListData = new GridData();
		hostListData.grabExcessVerticalSpace = true;
		hostListData.verticalAlignment = SWT.FILL;
		hostListData.grabExcessHorizontalSpace = true;
		hostListData.horizontalAlignment = SWT.FILL;
		hostListData.horizontalSpan = 1;
		hostList.setLayoutData(hostListData);

		ServerPanel serverPanel = new ServerPanel(parent,
				SWT.BORDER_SOLID,
				selectedConnection,
				hosts,
				shareManager,
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
}
