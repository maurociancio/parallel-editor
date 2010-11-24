package ar.noxit.paralleleditor.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ar.noxit.paralleleditor.eclipse.locator.IModel;
import ar.noxit.paralleleditor.eclipse.locator.Model;
import ar.noxit.paralleleditor.eclipse.model.ConnectionId;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.preferences.PreferenceConstants;
import ar.noxit.paralleleditor.eclipse.share.ILocalKernelListener;
import ar.noxit.paralleleditor.eclipse.share.ShareManager;
import ar.noxit.paralleleditor.eclipse.share.sync.ChatCallbackAdapter;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String CONNECTIONVIEW = "ar.noxit.paralleleditor.connectionview";

	// The plug-in ID
	public static final String PLUGIN_ID = "ParallelEditor-Eclipse"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// Share manager
	public static ShareManager shareManager = null;

	// hosts list
	public static IModel<List<ConnectionInfo>> hostsModel = null;

	// chat callback
	private static ChatCallbackAdapter chatCallback;

	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		chatCallback = new ChatCallbackAdapter();
		hostsModel = new Model<List<ConnectionInfo>>(new ArrayList<ConnectionInfo>());
		shareManager = new ShareManager(new LocalKernelListener(), chatCallback);
		loadHostList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		saveHostList();
		shareManager.dispose();
		chatCallback = null;
		hostsModel = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static ChatCallbackAdapter getChatCallback() {
		return chatCallback;
	}

	private void saveHostList() {
		Integer hostCount = 0;
		for (ConnectionInfo c : hostsModel.get()) {
			if (!c.getId().isLocal()) {
				String portString = new Integer(c.getId().getPort()).toString();
				String userName = c.getUsername();
				String hostString = c.getId().getHost();
				saveHost(hostCount, hostString, portString, userName);
				hostCount++;
			}
		}
		getPreferenceStore().setValue(PreferenceConstants.HOST_COUNT, hostCount);
	}

	private void saveHost(Integer hostNumber, String hostString, String portString, String userString) {
		IPreferenceStore preferenceStore = this.getPreferenceStore();
		preferenceStore.setValue(PreferenceConstants.REMOTE_HOST + hostNumber, hostString);
		preferenceStore.setValue(PreferenceConstants.REMOTE_PORT + hostNumber, portString);
		preferenceStore.setValue(PreferenceConstants.REMOTE_USER + hostNumber, userString);
	}

	private void loadHostList() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		Integer count = preferenceStore.getInt(PreferenceConstants.HOST_COUNT);
		List<ConnectionInfo> hostList = new ArrayList<ConnectionInfo>();
		for (int i = 0; i < count; i++) {
			int portString = preferenceStore.getInt(PreferenceConstants.REMOTE_PORT + i);
			String userName = preferenceStore.getString(PreferenceConstants.REMOTE_USER + i);
			String hostString = preferenceStore.getString(PreferenceConstants.REMOTE_HOST + i);
			hostList.add(new ConnectionInfo(new ConnectionId(hostString, portString), userName));
		}
		hostsModel.set(hostList);
	}

	private class LocalKernelListener implements ILocalKernelListener {

		private boolean added;
		private ConnectionInfo element;

		@Override
		public synchronized void onCreation(ConnectionInfo info) {
			if (!added) {
				this.element = info;

				List<ConnectionInfo> hosts = hostsModel.get();
				hosts.add(0, element);

				// para que se disparen los listeners
				hostsModel.set(hosts);

				this.added = true;
			}
		}

		@Override
		public synchronized void onDestroy() {
			if (added) {
				List<ConnectionInfo> hosts = hostsModel.get();
				hosts.remove(element);

				// para que se disparen los listeners
				hostsModel.set(hosts);

				added = false;
			}
		}
	}
}
