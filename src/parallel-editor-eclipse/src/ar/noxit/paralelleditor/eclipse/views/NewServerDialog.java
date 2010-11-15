package ar.noxit.paralelleditor.eclipse.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ar.noxit.paralelleditor.eclipse.views.ConnectionInfo.ConnectionId;

public class NewServerDialog extends Dialog {

	private ConnectionInfo result = null;

	public NewServerDialog(Shell parent, int style) {
		super(parent, style);
	}

	public ConnectionInfo open() {
		Shell parent = getParent();

		final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("New Connection");

		shell.setLayout(new GridLayout(2, true));

		Label label = new Label(shell, SWT.CENTER);
		label.setText("Insert the following information: ");
		GridData layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		label.setLayoutData(layoutData);

		new Label(shell, SWT.CENTER).setText("Hostname: ");
		final Text host = new Text(shell, SWT.Expand);

		new Label(shell, SWT.CENTER).setText("Port: ");
		final Text port = new Text(shell, SWT.NONE);

		// TODO sacar el valor desde la configuración
		port.setText("5000");

		new Label(shell, SWT.CENTER).setText("Username: ");
		final Text username = new Text(shell, SWT.NONE);
		// TODO sacar el valor desde la configuración
		username.setText("username");

		Button accept = new Button(shell, SWT.PUSH);
		accept.setText("Accept");
		accept.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				List<String> errors = validateFields(host, port, username);
				if (!errors.isEmpty()) {
					String error = getError(errors);

					MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
					messageBox.setMessage("Incorrect parameters:\n" + error);
					messageBox.open();
				} else {
					ConnectionId connectionId = new ConnectionId(host.getText(), Integer.valueOf(port.getText()));
					result = new ConnectionInfo(connectionId, username.getText());
					shell.dispose();
				}
			}
		});

		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});

		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		return result;
	}

	protected List<String> validateFields(final Text host, final Text port, final Text username) {
		final String hostString = host.getText();
		final String portString = port.getText();
		final String usernameString = username.getText();

		List<String> errors = new ArrayList<String>();

		try {
			Integer portInt = Integer.valueOf(portString);
			if ((portInt < 1) || (portInt > 65535)) {
				errors.add("Port should be between 1 and 65535");
			}
		} catch (NumberFormatException ex) {
			errors.add("Port is not a number");
		}

		if (hostString.isEmpty()) {
			errors.add("Hostname is empty.");
		}
		if (usernameString.isEmpty()) {
			errors.add("Username is empty.");
		}
		return errors;
	}

	protected String getError(List<String> errors) {
		String error = "";
		for (String current : errors) {
			error += current + "\n";
		}
		return error;
	}
}
