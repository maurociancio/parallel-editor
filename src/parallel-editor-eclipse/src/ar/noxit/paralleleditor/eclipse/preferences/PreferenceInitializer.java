package ar.noxit.paralleleditor.eclipse.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ar.noxit.paralleleditor.eclipse.Activator;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		store.setDefault(PreferenceConstants.DEFAULT_USERNAME, getUsername());
		store.setDefault(PreferenceConstants.LOCAL_SERVICE_PORT, 5000);
		store.setDefault(PreferenceConstants.LOCAL_SERVICE_HOSTNAME, "localhost");
	}

	protected String getUsername() {
		final String username = System.getProperty("user.name");
		if (username != null && !username.isEmpty())
			return username;
		else
			return "your_name_here";
	}
}
