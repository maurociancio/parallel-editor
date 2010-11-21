package ar.noxit.paralleleditor.eclipse.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ar.noxit.paralleleditor.eclipse.Activator;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Configuration for Parallel-Editor Eclipse Plugin");
	}

	public void createFieldEditors() {
		StringFieldEditor username = new StringFieldEditor(PreferenceConstants.DEFAULT_USERNAME,
				"Default &username used when creating local shares:",
				getFieldEditorParent());
		username.setEmptyStringAllowed(false);
		addField(username);

		IntegerFieldEditor port = new IntegerFieldEditor(PreferenceConstants.LOCAL_SERVICE_PORT,
				"&Port used when creating local shares:",
				getFieldEditorParent());
		port.setValidRange(1, 65535);
		addField(port);

		StringFieldEditor hostname = new StringFieldEditor(PreferenceConstants.LOCAL_SERVICE_HOSTNAME,
				"&Destination when connecting to local service:",
				getFieldEditorParent()) {
			@Override
			protected boolean doCheckState() {
				final String hostname = getTextControl().getText();
				return !hostname.contains(" ");
			}
		};
		hostname.setEmptyStringAllowed(false);
		addField(hostname);
	}

	public void init(IWorkbench workbench) {
	}
}