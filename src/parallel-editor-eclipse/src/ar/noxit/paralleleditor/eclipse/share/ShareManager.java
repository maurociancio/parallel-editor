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
package ar.noxit.paralleleditor.eclipse.share;

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
import ar.noxit.paralleleditor.eclipse.model.ConnectionId;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.model.ConnectionStatus;
import ar.noxit.paralleleditor.eclipse.preferences.PreferenceConstants;
import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.share.sync.IDocumentSession;
import ar.noxit.paralleleditor.eclipse.share.sync.IRemoteMessageCallback;
import ar.noxit.paralleleditor.eclipse.views.share.IConnectionFactory;
import ar.noxit.paralleleditor.eclipse.views.share.IShareManager;
import ar.noxit.paralleleditor.kernel.Kernel;
import ar.noxit.paralleleditor.kernel.basic.BasicKernel;
import ar.noxit.paralleleditor.kernel.basic.SynchronizerFactory;
import ar.noxit.paralleleditor.kernel.basic.UserListMerger;
import ar.noxit.paralleleditor.kernel.basic.sync.SynchronizerAdapterFactory;
import ar.noxit.paralleleditor.kernel.basic.userlist.DefaultUserListMerger;
import ar.noxit.paralleleditor.kernel.remote.KernelService;
import ar.noxit.paralleleditor.kernel.remote.SocketKernelService;

public class ShareManager implements IShareManager, IConnectionFactory {

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
	private JSession localApiSession = null;

	private ISession localSession = null;

	// callbacks locales
	private DocumentsAdapter localAdapter;

	private ConnectionInfo localInfo;

	private final ILocalKernelListener localKernelListener;

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

	// ///////////
	private final IChatCallback chatCallback;

	public ShareManager(ILocalKernelListener localKernelListener, IChatCallback chatCallback) {
		Assert.isNotNull(localKernelListener);
		Assert.isNotNull(chatCallback);

		this.localKernelListener = localKernelListener;
		this.chatCallback = chatCallback;
	}

	@Override
	public IDocumentSession createLocalShare(String docTitle, String initialContent,
			IRemoteMessageCallback remoteMessageCallback) {

		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);
		Assert.isNotNull(remoteMessageCallback);

		// verify title
		verifyDocTitleNotExists(docTitle);

		// create the service
		createServiceIfNotCreated();

		//
		this.localAdapter.installCallback(docTitle, remoteMessageCallback);

		try {
			// create the session
			JSession newSession = createLocalSessionIfNotExists();

			// local session
			this.localSession = newSession(localInfo, localApiSession, localAdapter);

			// set session
			this.localAdapter.setSession(newSession);

			// create the new document
			newSession.send(new RemoteNewDocumentRequest(docTitle, initialContent));
		} catch (Exception e) {
			// remove the added callback
			this.localAdapter.removeCallback(docTitle);

			// set null
			this.localInfo = null;

			// rethrow the exception
			throw new RuntimeException(e);
		}

		return new DocumentSession(docTitle, localApiSession, converter);
	}

	private Session newSession(ConnectionInfo localInfo, JSession localApiSession, DocumentsAdapter localAdapter) {
		Session newSession = new Session(localApiSession, localAdapter);
		newSession.installChatCallback(chatCallback);
		return newSession;
	}

	@Override
	public ISession connect(final ConnectionInfo info) {
		Assert.isNotNull(info);

		// connection id
		ConnectionId id = info.getId();
		// adapter
		DocumentsAdapter adapter = new DocumentsAdapter(converter, info);

		// callback for username
		adapter.installOnLoginFailureCallback(new LoginFailureCallback(info, this));

		// the newly created session
		JSession newSession = SessionFactory.newJSession(id.getHost(), id.getPort(), adapter);
		// log in to the kernel
		newSession.send(new RemoteLoginRequest(info.getUsername()));
		// store the remote session
		remoteSessions.put(info.getId(), newSession);

		ISession newRemoteSession = newSession(info, newSession, adapter);
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
			if (existsLocalConnection()) {
				return localSession;
			} else
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
			if (localApiSession != null)
				localApiSession.close();
		}

		this.localAdapter = null;
		this.localInfo = null;
		this.kernelService = null;
		this.localApiSession = null;
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
		stopLocalService();
	}

	private void verifyDocTitleNotExists(String docTitle) {
		if (localAdapter != null)
			localAdapter.verifyDocTitleNotExists(docTitle);
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
		if (localApiSession == null) {
			JSession newSession = SessionFactory.newJSession(getLocalHostname(), getLocalPort(), localAdapter);
			newSession.send(new RemoteLoginRequest(getUsername()));
			this.localApiSession = newSession;
		}
		return this.localApiSession;
	}

	protected void createServiceIfNotCreated() {
		if (this.kernelService == null) {
			// create service
			this.kernelService = newKernelService();
			this.kernelService.startService();

			// local info
			this.localInfo = new ConnectionInfo(new ConnectionId(getLocalHostname(), getLocalPort(), true),
					getUsername());

			// notify service created
			this.localKernelListener.onCreation(localInfo);

			// create adapter
			this.localAdapter = new DocumentsAdapter(converter, localInfo);
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
