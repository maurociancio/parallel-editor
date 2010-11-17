package ar.noxit.paralelleditor.eclipse.views;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ar.noxit.paralelleditor.eclipse.model.IModel;
import ar.noxit.paralelleditor.eclipse.model.IModel.IModelListener;
import ar.noxit.paralelleditor.eclipse.model.Model;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;

public class ServerPanel extends Composite {

	private IModel<ConnectionInfo> connectionInfo;

	private DocumentsPanel documentsPanel;
	private StatusPanel statusPanel;
	private Label noSelectionLabel;
	private StackLayout layoutVisibility;
	private Composite docsContainer;

	private final IRemoteConnectionFactory connectionFactory;

	private static final String STATUS_DISCONNECTED = "Disconnected";
	private static final String STATUS_CONNECTED = "Connected";
	private static final String STATUS_CONNECTING = "Connecting to server...";
	private static final String STATUS_DISCONNECTING = "Disconnecting...";

	private Model<List<DocumentElement>> usersModel = new Model<List<DocumentElement>>();

	public ServerPanel(Composite parent, int style,
			IModel<ConnectionInfo> connectionInfo,
			IRemoteConnectionFactory connectionFactory) {
		super(parent, style);

		// connection factory
		this.connectionFactory = connectionFactory;

		// connection factory
		this.connectionInfo = connectionInfo;

		// layout
		setLayout(new FillLayout(SWT.HORIZONTAL));

		// status
		this.statusPanel = new StatusPanel(this, SWT.NONE);

		docsContainer = new Composite(this, SWT.NONE);
		layoutVisibility = new StackLayout();
		docsContainer.setLayout(layoutVisibility);
		this.documentsPanel = new DocumentsPanel(docsContainer, SWT.NONE);
		this.noSelectionLabel = new Label(docsContainer, SWT.NONE);
		this.noSelectionLabel.setText("Please select a hostname");

		layoutVisibility.topControl = documentsPanel;
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
		layoutVisibility.topControl = (info != null) ? documentsPanel : noSelectionLabel;
		statusPanel.setVisible(info != null);
		docsContainer.layout();
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
			setLayout(new FillLayout());
			Group contenedor = new Group(this, SWT.NONE);
			contenedor.setText("Connection Details");
			GridLayout layoutContenedor = new GridLayout();
			layoutContenedor.numColumns = 2;
			// layoutContenedor.makeColumnsEqualWidth = true;
			contenedor.setLayout(layoutContenedor);

			// username
			Label userNameLabel = new Label(contenedor, SWT.NONE);
			userNameLabel.setText("Username:");

			GridData gridDataUserName = new GridData();
			gridDataUserName.grabExcessHorizontalSpace = true;
			gridDataUserName.horizontalAlignment = GridData.FILL;
			gridDataUserName.grabExcessVerticalSpace = true;

			this.userName = new Label(contenedor, SWT.NONE);
			userName.setLayoutData(gridDataUserName);

			// serverIP
			Label serverIPLabel = new Label(contenedor, SWT.CENTER);
			serverIPLabel.setText("Hostname:");

			GridData gridDataServerIp = new GridData();
			gridDataServerIp.grabExcessHorizontalSpace = true;
			gridDataServerIp.horizontalAlignment = GridData.FILL;
			gridDataServerIp.grabExcessVerticalSpace = true;

			this.serverIP = new Label(contenedor, SWT.NONE);
			serverIP.setLayoutData(gridDataServerIp);

			// serverPort
			Label serverPortLabel = new Label(contenedor, SWT.CENTER);
			serverPortLabel.setText("Server Port:");

			GridData gridDataServerPort = new GridData();
			gridDataServerPort.grabExcessHorizontalSpace = true;
			gridDataServerPort.horizontalAlignment = GridData.FILL;
			gridDataServerPort.grabExcessVerticalSpace = true;

			this.serverPort = new Label(contenedor, SWT.NONE);
			serverPort.setLayoutData(gridDataServerPort);

			this.connectionStatus = new Label(contenedor, SWT.NONE);
			this.connectionStatus.setText("                          "); // FIX
			GridData connectionData = new GridData();
			connectionData.grabExcessHorizontalSpace = true;
			connectionStatus.setLayoutData(connectionData);
			showStatus();

			// connect-disconnect button
			final Button connectButton = new Button(contenedor, SWT.CENTER);
			connectButton.setText("Connect");
			connectButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ConnectionInfo info = connectionInfo.get();

					ISession session = connectionFactory.connect(info);
					session.installUserListCallback(new UserListCallback(usersModel));
					session.requestDocumentList();
				}
			});

			final Button openEditor = new Button(contenedor, SWT.None);
			openEditor.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					EditorOpener.openNewEditor("titulo.java", "");
				}
			});
			openEditor.setText("editor");

			GridData gridDataButton = new GridData();
			gridDataButton.grabExcessHorizontalSpace = true;
			gridDataButton.horizontalAlignment = SWT.FILL;
			connectButton.setLayoutData(gridDataButton);

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

		private TreeViewer docTree;

		public DocumentsPanel(Composite parent, int style) {
			super(parent, style);
			setLayout(new FillLayout(SWT.HORIZONTAL));

			Group contenedor = new Group(this, SWT.NONE);
			contenedor.setText("Available Users & Docs");
			contenedor.setLayout(new FillLayout());

			this.docTree = new TreeViewer(contenedor, SWT.NONE);
			docTree.setLabelProvider(new TreeLabelProvider());
			docTree.setContentProvider(new DocumentTreeContentProvider());

			usersModel.addNewListener(new IModelListener() {

				@Override
				public void onUpdate() {
					redraw();
				}
			});
		}

		@Override
		public void redraw() {
			docTree.setInput(usersModel.get());
			super.redraw();
		}
	}
}
