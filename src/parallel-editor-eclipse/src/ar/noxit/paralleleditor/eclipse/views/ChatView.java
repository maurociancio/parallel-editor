package ar.noxit.paralleleditor.eclipse.views;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralleleditor.eclipse.Activator;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;
import ar.noxit.paralleleditor.eclipse.model.IModel;
import ar.noxit.paralleleditor.eclipse.model.IModel.IModelListener;

public class ChatView extends ViewPart {

	private final IModel<List<ConnectionInfo>> hosts = Activator.hostsModel;
	private final ShareManager shareManager = Activator.shareManager;

	private Text history;
	private ComboViewer server;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));

		{
			this.history = new Text(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER | SWT.WRAP | SWT.READ_ONLY);
			GridData historyLayout = new GridData();
			historyLayout.grabExcessHorizontalSpace = true;
			historyLayout.grabExcessVerticalSpace = true;
			historyLayout.horizontalAlignment = GridData.FILL;
			historyLayout.verticalAlignment = GridData.FILL;
			this.history.setLayoutData(historyLayout);

			final Composite target = new Composite(parent, SWT.NONE);
			target.setLayout(new GridLayout(3, false));
			GridData targetLayout = new GridData();
			targetLayout.grabExcessHorizontalSpace = true;
			targetLayout.horizontalAlignment = GridData.FILL;
			target.setLayoutData(targetLayout);
			{
				Text message = new Text(target, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
				GridData messageLayout = new GridData();
				messageLayout.grabExcessHorizontalSpace = true;
				messageLayout.horizontalAlignment = GridData.FILL;
				message.setLayoutData(messageLayout);

				this.server = new ComboViewer(target, SWT.DROP_DOWN | SWT.READ_ONLY);
				this.server.setContentProvider(new ArrayContentProvider());
				this.server.setLabelProvider(new ConnectionInfoLabelProvider(false));
				this.server.setFilters(new ViewerFilter[] { new ConnectedViewerFilter() });
				this.hosts.addNewListener(new IModelListener() {

					@Override
					public void onUpdate() {
						populateItems();
						target.layout();
					}
				});
				populateItems();

				Button send = new Button(target, SWT.PUSH);
				send.setText("Send");
			}
		}
	}

	private void populateItems() {
		this.server.setInput(hosts.get().toArray());
		this.server.refresh();
	}

	@Override
	public void setFocus() {
	}

	private class ConnectedViewerFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			return shareManager.isConnected(((ConnectionInfo) element).getId());
		}
	}
}
