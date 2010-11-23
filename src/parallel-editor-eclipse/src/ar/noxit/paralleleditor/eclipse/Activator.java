package ar.noxit.paralleleditor.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.ChatCallbackAdapter;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ILocalKernelListener;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ShareManager;
import ar.noxit.paralleleditor.eclipse.model.IModel;
import ar.noxit.paralleleditor.eclipse.model.Model;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
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

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
