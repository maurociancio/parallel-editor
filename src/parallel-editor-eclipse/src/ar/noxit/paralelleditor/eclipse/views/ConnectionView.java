package ar.noxit.paralelleditor.eclipse.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
		
				
		Label serverIPLabel = new Label(parent,SWT.CENTER);
		serverIPLabel.setText("Server IP:");
		Text serverIP = new Text(parent, SWT.NONE);
		
		Label serverPortLabel = new Label(parent,SWT.CENTER);
		serverPortLabel.setText("Server Port:");
		Text serverPort = new Text(parent,SWT.NONE);
		
		Button connectButton = new Button(parent,SWT.CENTER);
	}

	@Override
	public void setFocus() {
		//helloWorldTest.setFocus();
	}

}
