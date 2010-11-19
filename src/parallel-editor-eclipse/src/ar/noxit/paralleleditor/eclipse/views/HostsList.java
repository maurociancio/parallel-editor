package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

import ar.noxit.paralleleditor.eclipse.model.IModel;
import ar.noxit.paralleleditor.eclipse.model.IModel.IModelListener;

public class HostsList extends Composite {

	private final IModel<java.util.List<ConnectionInfo>> hostsModel;
	private final List hosts;
	private final ListViewer hostList;

	private final IRemoteConnectionFactory connectionFactory;

	public HostsList(Composite parent, int style,
			final IModel<java.util.List<ConnectionInfo>> hostsModel,
			final IModel<ConnectionInfo> selectedConnection,
			final IRemoteConnectionFactory connectionFactory) {
		super(parent, style);

		// connection factory
		this.connectionFactory = connectionFactory;

		this.hostsModel = hostsModel;
		setLayout(new FillLayout());

		// layout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		Group contenedor = new Group(this, SWT.NONE);
		contenedor.setLayout(layout);
		contenedor.setText("Available hosts");

		// hosts list
		this.hosts = new List(contenedor, SWT.BORDER | SWT.V_SCROLL);
		this.hosts.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = hosts.getSelectionIndex();
				if (selectionIndex != -1) {
					selectedConnection.set(hostsModel.get().get(selectionIndex));
				} else {
					selectedConnection.set(null);
				}
			}
		});

		// host list data layout
		GridData listGridData = new GridData();
		listGridData.grabExcessVerticalSpace = true;
		listGridData.verticalAlignment = SWT.FILL;
		listGridData.horizontalAlignment = SWT.FILL;
		listGridData.grabExcessHorizontalSpace = true;
		hosts.setLayoutData(listGridData);

		hostList = new ListViewer(hosts);
		hostList.setContentProvider(new ArrayContentProvider());
		hostList.setLabelProvider(new ConnectionInfoLabelProvider());

		hostsModel.addNewListener(new IModelListener() {

			@Override
			public void onUpdate() {
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						redraw();
					}
				});
			}
		});
		// items
		populateList();

		// buttons container
		Composite buttonsContainer = new Composite(contenedor, SWT.NONE);
		buttonsContainer.setLayout(new FillLayout(SWT.VERTICAL));
		GridData buttonsGridData = new GridData();
		buttonsGridData.horizontalAlignment = SWT.FILL;
		buttonsGridData.grabExcessHorizontalSpace = true;
		buttonsContainer.setLayoutData(buttonsGridData);

		Button newHost = new Button(buttonsContainer, SWT.PUSH);
		newHost.setText("Add hostname");
		newHost.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewServerDialog newServerDialog = new NewServerDialog(getShell(), 0);
				ConnectionInfo info = newServerDialog.open();

				if (info != null) {
					hostsModel.get().add(info);
					redraw();
				}
			}
		});

		Button deleteHost = new Button(buttonsContainer, SWT.PUSH);
		deleteHost.setText("Delete hostname");
		deleteHost.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int selection = hosts.getSelectionIndex();
				if (selection != -1) {
					java.util.List<ConnectionInfo> hosts = hostsModel.get();
					ConnectionInfo connectionInfo = hosts.get(selection);

					// check if connected
					if (!connectionFactory.isConnected(connectionInfo.getId())) {
						hosts.remove(selection);

						// para que se disparen los listeners
						hostsModel.set(hosts);

						selectedConnection.set(null);
						redraw();
					} else {
						MessageDialog.openError(Display.getDefault().getActiveShell(),
								"Cannot delete server",
								"You're connected to this server. Please disconnect and try again.");
					}
				}
			}
		});

		pack();
	}

	@Override
	public void redraw() {
		populateList();
		super.redraw();
	}

	protected void populateList() {
		hostList.setInput(hostsModel.get().toArray());
	}
}
