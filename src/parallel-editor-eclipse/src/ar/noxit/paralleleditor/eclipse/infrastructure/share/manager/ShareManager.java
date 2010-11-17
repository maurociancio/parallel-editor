package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralelleditor.eclipse.views.ConnectionInfo;
import ar.noxit.paralelleditor.eclipse.views.ConnectionInfo.ConnectionId;
import ar.noxit.paralelleditor.eclipse.views.ConnectionStatus;
import ar.noxit.paralelleditor.eclipse.views.IRemoteConnectionFactory;
import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.client.ProcessOperation;
import ar.noxit.paralleleditor.client.SessionFactory;
import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.converter.DefaultEditOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultRemoteDocumentOperationConverter;
import ar.noxit.paralleleditor.common.converter.DefaultSyncOperationConverter;
import ar.noxit.paralleleditor.common.messages.RemoteLoginRequest;
import ar.noxit.paralleleditor.common.messages.RemoteNewDocumentRequest;
import ar.noxit.paralleleditor.common.operation.DocumentOperation;
import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IShareManager;
import ar.noxit.paralleleditor.kernel.Kernel;
import ar.noxit.paralleleditor.kernel.basic.BasicKernel;
import ar.noxit.paralleleditor.kernel.basic.SynchronizerFactory;
import ar.noxit.paralleleditor.kernel.basic.UserListMerger;
import ar.noxit.paralleleditor.kernel.basic.sync.SynchronizerAdapterFactory;
import ar.noxit.paralleleditor.kernel.basic.userlist.DefaultUserListMerger;
import ar.noxit.paralleleditor.kernel.remote.KernelService;
import ar.noxit.paralleleditor.kernel.remote.SocketKernelService;

public class ShareManager implements IShareManager, IRemoteConnectionFactory {

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

	// converter
	private DefaultRemoteDocumentOperationConverter converter = new DefaultRemoteDocumentOperationConverter(
			new DefaultSyncOperationConverter(new DefaultEditOperationConverter()));

	@Override
	public IDocumentSession createLocalShare(
			final String docTitle,
			String initialContent,
			IRemoteMessageCallback remoteMessageCallback) {

		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);
		Assert.isNotNull(remoteMessageCallback);

		// create the service
		createServiceIfNotCreated();
		//
		callbacks.put(docTitle, remoteMessageCallback);
		// create the session
		JSession newSession = createSessionIfNotExists();
		// create the new document
		newSession.send(new RemoteNewDocumentRequest(docTitle, initialContent));

		return new IDocumentSession() {

			@Override
			public void onNewLocalMessage(Message<EditOperation> message) {
				localSession.send(converter.convert(new DocumentOperation(docTitle, message)));
			}
		};
	}

	@Override
	public void connect(ConnectionInfo info) {
		Assert.isNotNull(info);

		ConnectionId id = info.getId();
		JSession newSession = SessionFactory.newJSession(id.getHost(), id.getPort(), new Documents() {

			@Override
			public void process(CommandFromKernel command) {
				System.out.println(command);
			}
		});
		newSession.send(new RemoteLoginRequest(info.getUsername()));
		remoteSessions.put(info.getId(), newSession);
	}

	@Override
	public ConnectionStatus statusOf(ConnectionInfo info) {
		Assert.isNotNull(info);

		JSession session = remoteSessions.get(info.getId());
		if (session == null)
			return ConnectionStatus.DISCONNECTED;
		else
			return ConnectionStatus.CONNECTED;
	}

	public void dispose() {
		// TODO implementar
	}

	protected JSession createSessionIfNotExists() {
		if (localSession == null) {
			JSession newSession = SessionFactory.newJSession("localhost", 5000, new Documents() {

				@Override
				public void process(CommandFromKernel command) {
					System.out.println(command);

					if (command instanceof ProcessOperation) {
						ProcessOperation processOperation = (ProcessOperation) command;

						String docTitle = processOperation.title();
						callbacks.get(docTitle).onNewRemoteMessage(processOperation.msg());
					}
				}
			});

			newSession.send(new RemoteLoginRequest("becho"));
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
		SocketKernelService kernelService = new SocketKernelService(5000);
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
