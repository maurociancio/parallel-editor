package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.client.ProcessOperation;
import ar.noxit.paralleleditor.client.SessionFactory;
import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.converter.DefaultEditOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultRemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultSyncOperationConverter;
import ar.noxit.paralleleditor.common.messages.RemoteLoginRequest;
import ar.noxit.paralleleditor.common.messages.RemoteNewDocumentRequest;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IShareManager;
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

	// extract to configuration panel
	private static final String LOCALHOST = "localhost";
	private static final int LOCALPORT = 5000;
	private static final String LOCAL_USERNAME = "local_username";

	/**
	 * kernel service for local documents
	 */
	private KernelService kernelService = null;

	/**
	 * Connection to the local kernel (kernelService)
	 */
	private JSession localSession = null;

	/**
	 * Map of doctitle (local documents) to callback
	 */
	private Map<String, IRemoteMessageCallback> callbacks = new HashMap<String, IRemoteMessageCallback>();

	/**
	 * remote sessions
	 */
	private Map<ConnectionId, JSession> remoteSessions = new HashMap<ConnectionId, JSession>();

	/**
	 * remote sessions callbacks
	 */
	private Map<ConnectionId, ISession> remoteSessionsCallbacks = new HashMap<ConnectionId, ISession>();

	// converter
	private DefaultRemoteDocumentOperationConverter converter = new DefaultRemoteDocumentOperationConverter(
			new DefaultSyncOperationConverter(new DefaultEditOperationConverter()));

	@Override
	public IDocumentSession createLocalShare(String docTitle,
			String initialContent,
			IRemoteMessageCallback remoteMessageCallback) {

		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);
		Assert.isNotNull(remoteMessageCallback);

		// create the service
		createServiceIfNotCreated();

		//
		callbacks.put(docTitle, remoteMessageCallback);

		try {
			// create the session
			JSession newSession = createLocalSessionIfNotExists();

			// create the new document
			newSession.send(new RemoteNewDocumentRequest(docTitle, initialContent));
		} catch (Exception e) {
			// remove the added callback
			callbacks.remove(docTitle);

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
		RemoteDocumentsAdapter adapter = new RemoteDocumentsAdapter();
		// the newly created session
		JSession newSession = SessionFactory.newJSession(id.getHost(), id.getPort(), adapter);
		// log in to the kernel
		newSession.send(new RemoteLoginRequest(info.getUsername()));
		// store the remote session
		remoteSessions.put(info.getId(), newSession);

		ISession newRemoteSession = new RemoteSession(info, newSession, adapter);
		remoteSessionsCallbacks.put(id, newRemoteSession);
		return newRemoteSession;
	}

	@Override
	public ConnectionStatus statusOf(ConnectionId id) {
		Assert.isNotNull(id);

		JSession session = remoteSessions.get(id);
		if (session == null)
			return ConnectionStatus.DISCONNECTED;
		else
			return ConnectionStatus.CONNECTED;
	}

	@Override
	public ISession getSession(ConnectionId id) {
		Assert.isNotNull(id);

		return remoteSessionsCallbacks.get(id);
	}

	public void dispose() {
		// TODO implementar
	}

	protected JSession createLocalSessionIfNotExists() {
		if (localSession == null) {
			JSession newSession = SessionFactory.newJSession(LOCALHOST, LOCALPORT, new Documents() {

				@Override
				public void process(CommandFromKernel command) {
					if (command instanceof ProcessOperation) {
						ProcessOperation processOperation = (ProcessOperation) command;

						String docTitle = processOperation.title();
						callbacks.get(docTitle).onNewRemoteMessage(processOperation.msg());
					}
				}
			});

			newSession.send(new RemoteLoginRequest(LOCAL_USERNAME));
			this.localSession = newSession;
		}
		return this.localSession;
	}

	protected void createServiceIfNotCreated() {
		if (this.kernelService == null) {
			this.kernelService = newKernelService();
			this.kernelService.startService();
		}
	}

	protected KernelService newKernelService() {
		SocketKernelService kernelService = new SocketKernelService(LOCALPORT);
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
