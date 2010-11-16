package ar.noxit.paralelleditor.eclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import ar.noxit.paralelleditor.eclipse.model.IModel;

public class HostsList extends Composite {

	private IModel<java.util.List<ConnectionInfo>> hostsModel;
	private List hosts;

	public HostsList(Composite parent, int style,
			final IModel<java.util.List<ConnectionInfo>> hostsModel,
			final IModel<ConnectionInfo> selectedConnection) {
		super(parent, style);
		this.hostsModel = hostsModel;

		// layout
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		setLayout(layout);

		// hosts
		Label host = new Label(this, style);
		host.setText("Available hosts");

		// hosts list
		this.hosts = new List(this, SWT.BORDER | SWT.V_SCROLL);
		this.hosts.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = hosts.getSelectionIndex();
				if (selectionIndex != -1) {
					selectedConnection
							.set(hostsModel.get().get(selectionIndex));
				} else {
					selectedConnection.set(null);
				}
			}
		});

		//host list data layout
		GridData listGridData = new GridData();
		listGridData.grabExcessVerticalSpace = true;
		listGridData.verticalAlignment = SWT.FILL;
		listGridData.horizontalAlignment = SWT.FILL;
		listGridData.grabExcessHorizontalSpace = true;
		hosts.setLayoutData(listGridData);

		
		// items
		populateList();

		//buttons container 
		Composite buttonsContainer = new Composite(this, SWT.NONE);
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
				NewServerDialog newServerDialog = new NewServerDialog(
						getShell(), 0);
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
					hostsModel.get().remove(selection);
					selectedConnection.set(null);
					redraw();
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
		// items
		java.util.List<ConnectionInfo> list = hostsModel.get();
		String arrayItems[] = new String[list.size()];
		Integer i = 0;
		for (ConnectionInfo h : list) {
			arrayItems[i] = h.getId().getHost() + ":" + h.getId().getPort()
					+ " as " + h.getUsername();
			i = i + 1;
		}

		hosts.setItems(arrayItems);
	}
}
