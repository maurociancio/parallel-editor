package ar.noxit.paralleleditor.eclipse.views;

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

/**
 * A simple input dialog for soliciting an input string from the user.
 * <p>
 * This concrete dialog class can be instantiated as is, or further subclassed
 * as required.
 * </p>
 */
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

	/*
	 * (non-Javadoc) Method declared on Dialog.
	 */
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
		// do this here because setting the text will set enablement on the ok
		// button
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

		errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		errorMessageText.setBackground(errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		// Set the error message text
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
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

	/**
	 * Sets or clears the error message. If not <code>null</code>, the OK button
	 * is disabled.
	 * 
	 * @param errorMessage
	 *            the error message, or <code>null</code> to clear
	 * @since 3.0
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		if (errorMessageText != null && !errorMessageText.isDisposed()) {
			errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
			// Disable the error message text control if there is no error, or
			// no error text (empty or whitespace only). Hide it also to avoid
			// color change.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
			boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
			errorMessageText.setEnabled(hasError);
			errorMessageText.setVisible(hasError);
			errorMessageText.getParent().update();
			// Access the ok button by id, in case clients have overridden
			// button creation.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
			Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage == null);
			}
		}
	}

	/**
	 * Returns the style bits that should be used for the input text field.
	 * Defaults to a single line entry. Subclasses may override.
	 * 
	 * @return the integer style bits that should be used when creating the
	 *         input text
	 * 
	 * @since 3.4
	 */
	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
}
