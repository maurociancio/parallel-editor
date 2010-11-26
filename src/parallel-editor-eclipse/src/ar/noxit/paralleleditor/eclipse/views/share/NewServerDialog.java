/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ar.noxit.paralleleditor.eclipse.views.share;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import ar.noxit.paralleleditor.eclipse.model.ConnectionId;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.ShareManager;

public class NewServerDialog extends Dialog {

	private String hostNameValue = "";//$NON-NLS-1$
	private Integer portValue = 0;//$NON-NLS-1$
	private String usernameValue = "";//$NON-NLS-1$

	private IInputValidator hostNameValidator;
	private IInputValidator usernameValidator;

	private Text hostNameText;
	private Text usernameText;
	private Spinner portSlider;

	private Text errorMessageText;
	private String errorMessage;

	public NewServerDialog(Shell parentShell) {
		super(parentShell);

		hostNameValidator = new IInputValidator() {

			@Override
			public String isValid(String newText) {
				if (newText == null || newText.isEmpty())
					return "";
				else if (newText.contains(" "))
					return "Invalid hostname, it contains an space ' '.";
				else
					return null;
			}
		};
		usernameValidator = new IInputValidator() {

			@Override
			public String isValid(String newText) {
				if (newText == null || newText.isEmpty())
					return "";
				else
					return null;
			}
		};
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			hostNameValue = hostNameText.getText();
			portValue = portSlider.getSelection();
			usernameValue = usernameText.getText();
		} else {
			hostNameValue = null;
			portValue = null;
			usernameValue = null;
		}
		super.buttonPressed(buttonId);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("New remote connection");
	}

	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true).setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		hostNameText.setFocus();
	}

	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);

		// hostname
		newLabel("Hostname: ", parent, composite);
		hostNameText = newText(composite);

		// port
		newLabel("Port: ", parent, composite);
		portSlider = new Spinner(composite, SWT.BORDER);
		portSlider.setMaximum(65535);
		portSlider.setMinimum(1);
		portSlider.setSelection(5000);

		// username
		newLabel("Username: ", parent, composite);
		usernameText = newText(composite);
		usernameText.setText(usernameValue = ShareManager.getUsername());

		errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		setErrorMessage(errorMessage);

		applyDialogFont(composite);
		return composite;
	}

	private Text newText(Composite composite) {
		Text text = new Text(composite, getInputTextStyle());
		text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				validateInput();
			}
		});
		return text;
	}

	private Label newLabel(String label, Composite parent, Composite composite) {
		Label hostNamelabel = new Label(composite, SWT.WRAP);
		hostNamelabel.setText(label);

		GridData hostNameData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
				| GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		hostNameData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);

		hostNamelabel.setLayoutData(hostNameData);
		hostNamelabel.setFont(parent.getFont());

		return hostNamelabel;
	}

	protected void validateInput() {
		Set<String> errors = new HashSet<String>();
		errors.add(hostNameValidator.isValid(hostNameText.getText()));
		errors.add(usernameValidator.isValid(usernameText.getText()));

		errors.remove(null);
		if (!errors.isEmpty()) {
			errors.remove("");

			String errorMessage = "";
			for (String current : errors) {
				errorMessage = errorMessage + current + "\n";
			}
			setErrorMessage(errorMessage);
		} else {
			setErrorMessage(null);
		}
	}

	public ConnectionInfo getConnectionInfo() {
		return new ConnectionInfo(new ConnectionId(hostNameValue, portValue), usernameValue);
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		if (errorMessageText != null && !errorMessageText.isDisposed()) {
			errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$

			boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
			errorMessageText.setEnabled(hasError);
			errorMessageText.setVisible(hasError);
			errorMessageText.getParent().update();

			Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage == null);
			}
		}
	}

	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
}
