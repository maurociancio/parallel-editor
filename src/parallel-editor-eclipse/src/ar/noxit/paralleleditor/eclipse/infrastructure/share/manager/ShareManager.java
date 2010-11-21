package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.IPreferenceStore;

import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.client.SessionFactory;
import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.converter.DefaultEditOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultRemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultSyncOperationConverter;
import ar.noxit.paralleleditor.common.converter.RemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.messages.RemoteLoginRequest;
import ar.noxit.paralleleditor.common.messages.RemoteNewDocumentRequest;
import ar.noxit.paralleleditor.eclipse.Activator;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IShareManager;
import ar.noxit.paralleleditor.eclipse.preferences.PreferenceConstants;
import ar.noxit.paralleleditor.eclipse.views.ConnectionId;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.views.ConnectionStatus;
import ar.noxit.paralleleditor.eclipse.views.IRemoteConnectionFactory;
import ar.noxit.paralleleditor.kernel.Kernel;
import ar.noxit.paralleleditor.kernel.basic.BasicKernel;
import ar.noxit.paralleleditor.kernel.basic.SynchronizerFactory;
import ar.noxit.paralleleditor.kernel.basic.UserListMerger;
import ar.noxit.paralleleditor.kernel.basic.sync.SynchronizerAdapterFactory;
import ar.noxit.paralleleditor.kernel.basic.userlist.DefaultUserListMerger;
import ar.noxit.paralleleditor.kernel.remote.KernelService;
import ar.noxit.paralleleditor.kernel.remote.SocketKernelService;

public class ShareManager implements IShareManager, IRemoteConnectionFactory {

	// converter
	private final RemoteDocumentOperationConverter converter = new DefaultRemoteDocumentOperationConverter(
			new DefaultSyncOperationConverter(new DefaultEditOperationConverter()));

	// ///////////
	// LOCAL
	// ///////////
	/**
	 * kernel service for local documents
	 */
	private KernelService kernelService = null;

	/**
	 * Connection to the local kernel (kernelService)
	 */
	private JSession localSession = null;

	private final ILocalKernelListener localKernelListener;

	// callbacks locales
	private DocumentsAdapter localAdapter = new DocumentsAdapter(converter);

	// current username
	private String currentUsername = null;

	// ///////////
	// REMOTE
	// ///////////
	/**
	 * remote sessions
	 */
	private final Map<ConnectionId, JSession> remoteSessions = new HashMap<ConnectionId, JSession>();

	/**
	 * remote sessions callbacks
	 */
	private final Map<ConnectionId, ISession> remoteSessionsCallbacks = new HashMap<ConnectionId, ISession>();

	public ShareManager(ILocalKernelListener localKernelListener) {
		Assert.isNotNull(localKernelListener);

		this.localKernelListener = localKernelListener;
	}

	@Override
	public IDocumentSession createLocalShare(String docTitle, String initialContent,
			IRemoteMessageCallback remoteMessageCallback) {

		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);
		Assert.isNotNull(remoteMessageCallback);

		// verify title
		localAdapter.verifyDocTitleNotExists(docTitle);

		// create the service
		createServiceIfNotCreated();

		//
		this.localAdapter.installCallback(docTitle, remoteMessageCallback);

		try {
			// create the session
			JSession newSession = createLocalSessionIfNotExists();

			// set session
			localAdapter.setSession(newSession);

			// create the new document
			newSession.send(new RemoteNewDocumentRequest(docTitle, initialContent));

			// set the current username
			currentUsername = getUsername();
		} catch (Exception e) {
			// remove the added callback
			this.localAdapter.removeCallback(docTitle);

			// rethrow the exception
			throw new RuntimeException(e);
		}

		return new DocumentSession(docTitle, localSession, converter);
	}

	@Override
	public ISession connect(ConnectionInfo info) {
		Assert.isNotNull(info);

		// connection id
		ConnectionId id = info.getId();
		// adapter
		DocumentsAdapter adapter = new DocumentsAdapter(converter);
		// the newly created session
		JSession newSession = SessionFactory.newJSession(id.getHost(), id.getPort(), adapter);
		// log in to the kernel
		newSession.send(new RemoteLoginRequest(info.getUsername()));
		// store the remote session
		remoteSessions.put(info.getId(), newSession);

		ISession newRemoteSession = new Session(info, newSession, adapter);
		remoteSessionsCallbacks.put(id, newRemoteSession);
		return newRemoteSession;
	}

	@Override
	public ConnectionStatus statusOf(ConnectionId id) {
		Assert.isNotNull(id);

		if (isLocalConnection(id)) {
			if (existsLocalConnection())
				return ConnectionStatus.CONNECTED;
		} else {
			JSession session = remoteSessions.get(id);
			if (session != null)
				return ConnectionStatus.CONNECTED;
		}
		return ConnectionStatus.DISCONNECTED;
	}

	@Override
	public ISession getSession(ConnectionId id) {
		Assert.isNotNull(id);

		if (id.isLocal()) {
			if (existsLocalConnection())
				return new Session(new ConnectionInfo(id, currentUsername), localSession, this.localAdapter);
			else
				return null;
		} else {
			return remoteSessionsCallbacks.get(id);
		}
	}

	@Override
	public boolean isConnected(ConnectionId id) {
		Assert.isNotNull(id);

		return remoteSessions.containsKey(id) || (isLocalConnection(id) && existsLocalConnection());
	}

	@Override
	public void stopLocalService() {
		if (existsLocalConnection()) {
			if (kernelService != null)
				kernelService.stopService();
			if (localKernelListener != null)
				localKernelListener.onDestroy();
			if (localAdapter != null)
				localAdapter.dispose();
			if (localSession != null)
				localSession.close();

			localAdapter = new DocumentsAdapter(converter);
			currentUsername = null;
		}
		this.kernelService = null;
		this.localSession = null;
	}

	@Override
	public void disconnect(ConnectionId id) {
		ISession session = getSession(id);
		if (session != null) {
			session.close();
			remoteSessions.remove(id);
		}
	}

	public void dispose() {
		// TODO implementar
	}

	private boolean isLocalConnection(ConnectionId id) {
		return id.isLocal();
	}

	private boolean existsLocalConnection() {
		return kernelService != null;
	}

	public static String getLocalHostname() {
		return getStore().getString(PreferenceConstants.LOCAL_SERVICE_HOSTNAME);
	}

	public static String getUsername() {
		return getStore().getString(PreferenceConstants.DEFAULT_USERNAME);
	}

	public static int getLocalPort() {
		return getStore().getInt(PreferenceConstants.LOCAL_SERVICE_PORT);
	}

	public static IPreferenceStore getStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	protected JSession createLocalSessionIfNotExists() {
		if (localSession == null) {
			JSession newSession = SessionFactory.newJSession(getLocalHostname(), getLocalPort(), localAdapter);
			newSession.send(new RemoteLoginRequest(getUsername()));
			this.localSession = newSession;
		}
		return this.localSession;
	}

	protected void createServiceIfNotCreated() {
		if (this.kernelService == null) {
			this.kernelService = newKernelService();
			this.kernelService.startService();
			this.localKernelListener.onCreation();
		}
	}

	protected KernelService newKernelService() {
		SocketKernelService kernelService = new SocketKernelService(getLocalPort());
		kernelService.setKernel(newKernel());
		return kernelService;
	}

	protected Kernel newKernel() {
		BasicKernel basicKernel = new BasicKernel();
		basicKernel.setTimeout(5000);
		basicKernel.setSync(newSyncFactory());
		basicKernel.setUserListMerger(newUserListMerger());

		return basicKernel;
	}

	protected SynchronizerFactory newSyncFactory() {
		SynchronizerAdapterFactory synchronizerAdapterFactory = new SynchronizerAdapterFactory();
		synchronizerAdapterFactory.setStrategy(new BasicXFormStrategy());

		return synchronizerAdapterFactory;
	}

	protected UserListMerger newUserListMerger() {
		DefaultUserListMerger defaultUserListMerger = new DefaultUserListMerger();
		defaultUserListMerger.setTimeout(5000);
		return defaultUserListMerger;
	}
}
