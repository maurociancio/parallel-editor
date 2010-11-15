package ar.noxit.paralelleditor.eclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ar.noxit.paralelleditor.eclipse.model.IModel;
import ar.noxit.paralelleditor.eclipse.model.IModel.IModelListener;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;

public class ServerPanel extends Composite {

	private IModel<ConnectionInfo> connectionInfo;

	private DocumentsPanel documentsPanel;
	private StatusPanel statusPanel;
	private Label noSelectionLabel;

	// TODO no instanciar
	private IRemoteConnectionFactory connectionFactory = new ShareManager();

	private static final String STATUS_DISCONNECTED = "Disconnected";
	private static final String STATUS_CONNECTED = "Connected";
	private static final String STATUS_CONNECTING = "Connecting to server...";
	private static final String STATUS_DISCONNECTING = "Disconnecting...";

	public ServerPanel(Composite parent, int style, IModel<ConnectionInfo> connectionInfo) {
		super(parent, style);
		this.connectionInfo = connectionInfo;

		this.statusPanel = new StatusPanel(parent, SWT.NONE);
		this.documentsPanel = new DocumentsPanel(parent, SWT.NONE);
		this.noSelectionLabel = new Label(parent, SWT.NONE);
		this.noSelectionLabel.setText("Please select a hostname");

		connectionInfo.addNewListener(new IModelListener() {

			@Override
			public void onUpdate() {
				redraw();
			}
		});

		determineVisibility();
	}

	private void determineVisibility() {
		ConnectionInfo info = connectionInfo.get();

		documentsPanel.setVisible(info != null);
		statusPanel.setVisible(info != null);
		noSelectionLabel.setVisible(info == null);
	}

	@Override
	public void redraw() {
		determineVisibility();
		documentsPanel.redraw();
		noSelectionLabel.redraw();
		statusPanel.redraw();
		super.redraw();
	}

	private class StatusPanel extends Composite {

		private Label userName;
		private Label serverIP;
		private Label serverPort;
		private Label connectionStatus;

		public StatusPanel(Composite parent, int style) {
			super(parent, style);

			GridLayout layoutContenedor = new GridLayout();
			layoutContenedor.numColumns = 2;
			setLayout(layoutContenedor);

			Label userNameLabel = new Label(this, SWT.CENTER);
			userNameLabel.setText("Username:");

			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;

			this.userName = new Label(this, SWT.NONE);
			userName.setLayoutData(gridData);

			Label serverIPLabel = new Label(this, SWT.CENTER);
			serverIPLabel.setText("Hostname:");

			this.serverIP = new Label(this, SWT.NONE);
			serverIP.setLayoutData(gridData);

			Label serverPortLabel = new Label(this, SWT.CENTER);
			serverPortLabel.setText("Server Port:");

			this.serverPort = new Label(this, SWT.NONE);
			serverPort.setLayoutData(gridData);

			this.connectionStatus = new Label(this, SWT.NONE);
			this.connectionStatus.setText("                   "); // FIX
			GridData connectionData = new GridData();
			connectionData.grabExcessHorizontalSpace = true;
			// connectionStatus.setLayoutData(connectionData);
			showStatus();

			final Button connectButton = new Button(this, SWT.CENTER);
			connectButton.setText("Connect");
			connectButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ConnectionInfo info = connectionInfo.get();

					connectionFactory.connect(info);
				}
			});

			updateTexts();
		}

		protected void showStatus() {
			final Color colorYelllow = new Color(this.getDisplay(), 255, 255, 0);
			final Color colorGreen = new Color(this.getDisplay(), 0, 255, 0);

			ConnectionInfo info = connectionInfo.get();
			if (info != null) {
				ConnectionStatus status = connectionFactory.statusOf(info);
				if (status.equals(ConnectionStatus.CONNECTED)) {
					connectionStatus.setText(STATUS_CONNECTED);
					connectionStatus.setBackground(colorGreen);
				} else {
					connectionStatus.setText(STATUS_DISCONNECTED);
					connectionStatus.setBackground(colorYelllow);
				}
			}
		}

		private void updateTexts() {
			ConnectionInfo info = connectionInfo.get();
			if (info != null) {
				userName.setText(info.getUsername());
				serverPort.setText(String.valueOf(info.getId().getPort()));
				serverIP.setText(info.getId().getHost());
			}
		}

		@Override
		public void redraw() {
			showStatus();
			updateTexts();
			super.redraw();
		}
	}

	private class DocumentsPanel extends Composite {

		public DocumentsPanel(Composite parent, int style) {
			super(parent, style);
		}
	}
}
