package ar.noxit.paralelleditor.eclipse.views;


import org.eclipse.swt.SWT;
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
		/*helloWorldTest = new Button(parent, SWT.PUSH);
		
		SelectionAdapter ad = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("You clicked me!");
			}
		};
		helloWorldTest.addSelectionListener(ad);
		helloWorldTest.setText("Hello World SWT");
		helloWorldTest.pack();
		*/
		
		Composite contenedor = new Composite(parent, SWT.BORDER_SOLID);
		//contenedor.setSize(200, 100);
		
		GridLayout layoutContendor = new GridLayout();
		layoutContendor.numColumns = 2;
		contenedor.setLayout(layoutContendor);
				
		Label serverIPLabel = new Label(contenedor,SWT.CENTER);
		serverIPLabel.setText("Server IP:");
		
		Text serverIP = new Text(contenedor, SWT.NONE);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace=true;
		gridData.horizontalAlignment = GridData.FILL;
		serverIP.setLayoutData(gridData);

		
		Label serverPortLabel = new Label(contenedor,SWT.CENTER);
		serverPortLabel.setText("Server Port:");
		Text serverPort = new Text(contenedor,SWT.NONE);
		serverPort.setLayoutData(gridData);
		
		Button connectButton = new Button(contenedor,SWT.CENTER);
		connectButton.setText("Conectar");
		
/*		Button disconnectButton = new Button(contenedor,SWT.CENTER);
		connectButton.setText("");
*/		
	//	contenedor.pack();
	}

	@Override
	public void setFocus() {
		//helloWorldTest.setFocus();
	}

}
