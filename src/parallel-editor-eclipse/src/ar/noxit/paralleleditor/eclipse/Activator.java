package ar.noxit.paralleleditor.eclipse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.ChatCallbackAdapter;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ILocalKernelListener;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;
import ar.noxit.paralleleditor.eclipse.model.IModel;
import ar.noxit.paralleleditor.eclipse.model.Model;
import ar.noxit.paralleleditor.eclipse.views.ConnectionId;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	private static final String REMOTE_HOST = "remoteHost";

	private static final String REMOTE_USER = "remoteUser";

	private static final String REMOTE_PORT = "remotePort";

	private static final String HOST_COUNT = "hostCount";

	public static final String CONNECTIONVIEW = "ar.noxit.paralleleditor.connectionview";

	// The plug-in ID
	public static final String PLUGIN_ID = "ParallelEditor-Eclipse"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	// Share manager
	public static ShareManager shareManager = null;

	// hosts list
	public static IModel<List<ConnectionInfo>> hostsModel = null;

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
		getPreferenceStore().setValue(HOST_COUNT, hostCount);
	}

	private void saveHost(Integer hostNumber, String hostString, String portString, String userString) {
		IPreferenceStore preferenceStore = this.getPreferenceStore();
		preferenceStore.setValue(REMOTE_HOST + hostNumber, hostString);
		preferenceStore.setValue(REMOTE_PORT + hostNumber, portString);
		preferenceStore.setValue(REMOTE_USER + hostNumber, userString);
	}

	private void loadHostList() {
		IPreferenceStore preferenceStore = getPreferenceStore();
		Integer count = preferenceStore.getInt(HOST_COUNT);
		List<ConnectionInfo> hostList = new ArrayList<ConnectionInfo>();
		for (int i = 0; i < count; i++) {
			int portString = preferenceStore.getInt(REMOTE_PORT + i);
			String userName = preferenceStore.getString(REMOTE_USER + i);
			String hostString = preferenceStore.getString(REMOTE_HOST + i);
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
