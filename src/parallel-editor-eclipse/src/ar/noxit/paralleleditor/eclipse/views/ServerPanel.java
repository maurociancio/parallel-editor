package ar.noxit.paralleleditor.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.SubscriptionAlreadyExistsException;
import ar.noxit.paralleleditor.eclipse.model.IModel;
import ar.noxit.paralleleditor.eclipse.model.IModel.IModelListener;
import ar.noxit.paralleleditor.eclipse.model.Model;

public class ServerPanel extends Composite {

	private IModel<ConnectionInfo> connectionInfo;

	private Composite hostComposite;
	private StackLayout layoutVisibility;

	private UsersPanel usersPanel;
	private StatusPanel statusPanel;
	private DocumentsPanel documentsPanel;

	private Label noSelectionLabel;

	private final IRemoteConnectionFactory connectionFactory;
	private final IRemoteDocumentShare remoteDocumentShare;

	private static final String STATUS_DISCONNECTED = "Disconnected";
	private static final String STATUS_CONNECTED = "Connected";
	private static final String STATUS_CONNECTING = "Connecting to server...";
	private static final String STATUS_DISCONNECTING = "Disconnecting...";

	private IModel<List<DocumentElement>> usersModel = new Model<List<DocumentElement>>(
			new ArrayList<DocumentElement>());
	private IModel<List<String>> docsModel = new Model<List<String>>(new ArrayList<String>());

	public ServerPanel(Composite parent, int style, IModel<ConnectionInfo> connectionInfo,
			IRemoteConnectionFactory connectionFactory, IRemoteDocumentShare remoteDocumentShare) {
		super(parent, style);

		// connection factory
		this.connectionFactory = connectionFactory;
		this.remoteDocumentShare = remoteDocumentShare;

		// connection info
		this.connectionInfo = connectionInfo;

		// layout
		this.layoutVisibility = new StackLayout();
		setLayout(layoutVisibility);

		// composite de: documents, users y server info
		this.hostComposite = new Composite(this, SWT.NONE);
		this.hostComposite.setLayout(new FillLayout(SWT.HORIZONTAL));

		// status
		this.statusPanel = new StatusPanel(hostComposite, SWT.NONE);
		// user panel
		this.usersPanel = new UsersPanel(hostComposite, SWT.NONE);
		// docs panel
		this.documentsPanel = new DocumentsPanel(hostComposite, SWT.NONE);

		this.noSelectionLabel = new Label(this, SWT.NONE);
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
		layoutVisibility.topControl = info != null ? hostComposite : noSelectionLabel;
		layout();
	}

	@Override
	public void redraw() {
		determineVisibility();
		usersPanel.redraw();
		noSelectionLabel.redraw();
		statusPanel.redraw();
		documentsPanel.redraw();
		super.redraw();
	}

	private class StatusPanel extends Composite {

		private Label userName;
		private Label serverIP;
		private Label serverPort;
		private Label connectionStatus;
		private Button connectButton;

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
			connectButton = new Button(contenedor, SWT.CENTER);
			connectButton.setText("Connect");
			connectButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ConnectionInfo info = connectionInfo.get();
					if (!connectionFactory.isConnected(info.getId()))
						connect(info);
					else
						disconnect(info);
					redraw();
				}

				public void connect(ConnectionInfo info) {
					try {
						ISession session = connectionFactory.connect(info);
						session.installUserListCallback(new UserListCallback(usersModel));
						session.requestUserList();
						session.installDocumentListCallback(new DocumentListCallback(docsModel));
						session.requestDocumentList();
					} catch (Exception ex) {
						// TODO log here the full stacktrace
						MessageDialog.openError(Display.getDefault().getActiveShell(),
								"Cannot connect to collaboration server",
								"It probably means that the remote host or port you entered is invalid. "
										+ "Please check the configuration.");
					}
				}

				public void disconnect(ConnectionInfo info) {
					connectionFactory.removeSession(info.getId());

					usersModel.set(new ArrayList<DocumentElement>());
					docsModel.set(new ArrayList<String>());
				}
			});

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
				ConnectionStatus status = connectionFactory.statusOf(info.getId());
				if (status.equals(ConnectionStatus.CONNECTED)) {
					connectionStatus.setText(STATUS_CONNECTED);
					connectionStatus.setBackground(colorGreen);
					connectButton.setText("Disconnect");
				} else {
					connectionStatus.setText(STATUS_DISCONNECTED);
					connectionStatus.setBackground(colorYelllow);
					connectButton.setText("Connect");
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

	private class UsersPanel extends Composite {

		private TreeViewer docTree;

		public UsersPanel(Composite parent, int style) {
			super(parent, style);
			setLayout(new FillLayout(SWT.HORIZONTAL));

			Group contenedor = new Group(this, SWT.NONE);
			contenedor.setText("Available Users");
			contenedor.setLayout(new FillLayout());

			this.docTree = new TreeViewer(contenedor, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
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

	private class DocumentsPanel extends Composite {

		private ListViewer documents;

		public DocumentsPanel(Composite parent, int style) {
			super(parent, style);
			setLayout(new FillLayout(SWT.HORIZONTAL));

			Group contenedor = new Group(this, SWT.NONE);
			contenedor.setText("Available Docs");
			contenedor.setLayout(new FillLayout());

			this.documents = new ListViewer(contenedor, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			this.documents.setLabelProvider(new DocumentLabelProvider());
			this.documents.setContentProvider(new ArrayContentProvider());
			this.documents.addDoubleClickListener(new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					String[] selection = documents.getList().getSelection();

					if (selection != null && selection.length == 1) {
						ConnectionId id = connectionInfo.get().getId();

						try {
							ISession session = connectionFactory.getSession(id);
							if (session != null) {
								session.installSubscriptionResponseCallback(
										new SubscriptionCallback(remoteDocumentShare));
								session.subscribe(selection[0]);
							}
						}
						catch (SubscriptionAlreadyExistsException e) {
							MessageDialog.openError(Display.getDefault().getActiveShell(),
									"Subscription to document already exists",
									"You are already subscribed to this document.");
						}
					}
				}
			});
			docsModel.addNewListener(new IModelListener() {

				@Override
				public void onUpdate() {
					redraw();
				}
			});
		}

		@Override
		public void redraw() {
			this.documents.setInput(docsModel.get().toArray());
			super.redraw();
		}
	}
}
