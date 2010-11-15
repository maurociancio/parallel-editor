package ar.noxit.paralelleditor.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import ar.noxit.paralelleditor.eclipse.model.IModel;
import ar.noxit.paralelleditor.eclipse.model.Model;

public class ConnectionView extends ViewPart {

	public ConnectionView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		// ConnectionPanel panel = new ConnectionPanel(parent, SWT.NONE);

		List<ConnectionInfo> connections = new ArrayList<ConnectionInfo>();
		IModel<ConnectionInfo> selectedConnection = new Model<ConnectionInfo>();

		new HostsList(parent, SWT.NONE, Model.of(connections), selectedConnection);
		new ServerPanel(parent, SWT.NONE, selectedConnection);

		Text infoConsole = new Text(parent, SWT.MULTI | SWT.V_SCROLL);
		infoConsole.setSize(100, 100);
		infoConsole.setEditable(true);
		infoConsole.setText("1.. 2.. 3.. Testing\n a ver si anda\n a ver si anda\n a ver si anda\n a ver si anda\n");

		parent.pack();
	}

	@Override
	public void setFocus() {
		// helloWorldTest.setFocus();
	}

	private class ConnectionPanel extends Composite {

		private boolean isConnected = false;
		private static final String STATUS_DISCONNECTED = "Disconnected";
		private static final String STATUS_CONNECTED = "Connected";
		private static final String STATUS_CONNECTING = "Connecting to server...";
		private static final String STATUS_DISCONNECTING = "Disconnecting...";

		public ConnectionPanel(final Composite parent, int style) {
			super(parent, style);
			// contenedor.setSize(200, 100);

			GridLayout layoutContendor = new GridLayout();
			layoutContendor.numColumns = 2;
			setLayout(layoutContendor);

			Label userNameLabel = new Label(this, SWT.CENTER);
			userNameLabel.setText("Username:");

			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;

			Text userName = new Text(this, SWT.NONE);
			userName.setLayoutData(gridData);

			Label serverIPLabel = new Label(this, SWT.CENTER);
			serverIPLabel.setText("Server IP:");

			Text serverIP = new Text(this, SWT.NONE);
			serverIP.setLayoutData(gridData);

			Label serverPortLabel = new Label(this, SWT.CENTER);
			serverPortLabel.setText("Server Port:");
			Text serverPort = new Text(this, SWT.NONE);
			serverPort.setLayoutData(gridData);

			final Color colorYelllow = new Color(this.getDisplay(), 255, 255, 0);
			final Color colorGreen = new Color(this.getDisplay(), 0, 255, 0);
			final Label connectionStatus = new Label(this, SWT.CENTER);
			connectionStatus.setText(STATUS_DISCONNECTED);
			connectionStatus.setBackground(colorYelllow);

			final Button connectButton = new Button(this, SWT.CENTER);
			connectButton.setText("Conectar");
			connectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (!isConnected) {
						System.out.println("You clicked conectar!");
						connectionStatus.setText(STATUS_CONNECTING);
						BusyIndicator.showWhile(connectButton.getDisplay(), new ConnectorThread());
						connectionStatus.setBackground(colorGreen);
						connectionStatus.setText(STATUS_CONNECTED);
						isConnected = true;
					} else {
						System.out.println("You clicked conectar!");
						connectionStatus.setText(STATUS_DISCONNECTING);
						BusyIndicator.showWhile(connectButton.getDisplay(), new ConnectorThread());
						connectionStatus.setBackground(colorYelllow);
						connectionStatus.setText(STATUS_DISCONNECTED);
						isConnected = false;
					}
				}
			});

			final Button prueba = new Button(this, SWT.PUSH);
			prueba.setText("Editor");
			prueba.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					NewServerDialog newServerDialog = new NewServerDialog(parent.getShell(), 0);
					newServerDialog.open();

					IWorkspace ws = ResourcesPlugin.getWorkspace();
					IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
					IProject project =
							ws.getRoot().getProject("Test");

					if (!project.isOpen())
						try {
							project.open(null);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					String path = "src/Caca.java";

					IPath location = project.getProjectRelativePath().append(path);
					IFile file = project.getFile(location);
					IWorkbenchPage page = window.getActivePage();
					if (page != null)
						try {
							IDE.openEditor(page, file, true);
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			});

		}

	}

	private class ConnectorThread extends Thread {

		@Override
		public void run() {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
