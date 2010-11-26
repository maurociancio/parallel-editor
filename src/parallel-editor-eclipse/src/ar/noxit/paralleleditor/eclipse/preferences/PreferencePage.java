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