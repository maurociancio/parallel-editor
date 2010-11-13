package ar.noxit.paralelleditor.eclipse.views;


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
import org.eclipse.ui.part.ViewPart;

public class ConnectionView extends ViewPart {

	
	public ConnectionView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		ConnectionPanel panel = new ConnectionPanel(parent, SWT.NONE);

		Text infoConsole = new Text(parent, SWT.MULTI | SWT.V_SCROLL);
		infoConsole.setSize(100, 100);
		infoConsole.setEditable(true);
		infoConsole.setText("1.. 2.. 3.. Testing\n a ver si anda\n a ver si anda\n a ver si anda\n a ver si anda\n");

		parent.pack();
	}

	@Override
	public void setFocus() {
		//helloWorldTest.setFocus();
	}
	
	private class ConnectionPanel extends Composite {


		private boolean isConnected = false;
		private final String STATUS_DISCONNECTED = "Disconnected";
		private final String STATUS_CONNECTED = "Connected";
		private final String STATUS_CONNECTING = "Connecting to server...";
		private final String STATUS_DISCONNECTING = "Disconnecting...";
		
		
		public ConnectionPanel(Composite parent, int style) {
			super(parent, style);
			//contenedor.setSize(200, 100);
			
			GridLayout layoutContendor = new GridLayout();
			layoutContendor.numColumns = 2;
			setLayout(layoutContendor);

			Label userNameLabel = new Label(this,SWT.CENTER);
			userNameLabel.setText("Username:");

			GridData gridData = new GridData();
			gridData.grabExcessHorizontalSpace=true;
			gridData.horizontalAlignment = GridData.FILL;

			Text userName = new Text(this,SWT.NONE);
			userName.setLayoutData(gridData);
			
			Label serverIPLabel = new Label(this,SWT.CENTER);
			serverIPLabel.setText("Server IP:");
			
			Text serverIP = new Text(this, SWT.NONE);
			serverIP.setLayoutData(gridData);

			
			Label serverPortLabel = new Label(this,SWT.CENTER);
			serverPortLabel.setText("Server Port:");
			Text serverPort = new Text(this,SWT.NONE);
			serverPort.setLayoutData(gridData);
			

			final Color colorYelllow = new Color(this.getDisplay(), 255, 255, 0);
			final Color colorGreen = new Color(this.getDisplay(),0,255,0);
			final Label connectionStatus = new Label(this,SWT.CENTER);
			connectionStatus.setText(STATUS_DISCONNECTED);
			connectionStatus.setBackground(colorYelllow);
			
			final Button connectButton = new Button(this,SWT.CENTER);
			connectButton.setText("Conectar");
			connectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					if (!isConnected){
						System.out.println("You clicked conectar!");
						connectionStatus.setText(STATUS_CONNECTING);
						BusyIndicator.showWhile(connectButton.getDisplay(),new ConnectorThread());
						connectionStatus.setBackground(colorGreen);
						connectionStatus.setText(STATUS_CONNECTED);
						isConnected=true;
					} else {
						System.out.println("You clicked conectar!");
						connectionStatus.setText(STATUS_DISCONNECTING);
						BusyIndicator.showWhile(connectButton.getDisplay(),new ConnectorThread());
						connectionStatus.setBackground(colorYelllow);
						connectionStatus.setText(STATUS_DISCONNECTED);
						isConnected=false;
					}
				}
			});
			
		}
	
		
	}
	
	
	private class ConnectorThread extends Thread {
		
		@Override
		public void run(){
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}

}
