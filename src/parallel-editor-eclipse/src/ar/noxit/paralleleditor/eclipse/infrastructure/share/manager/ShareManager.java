package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.client.CommandFromKernel;
import ar.noxit.paralleleditor.client.Documents;
import ar.noxit.paralleleditor.client.JSession;
import ar.noxit.paralleleditor.client.SessionFactory;
import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.messages.RemoteLoginRequest;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.IShareManager;
import ar.noxit.paralleleditor.kernel.Kernel;
import ar.noxit.paralleleditor.kernel.basic.BasicKernel;
import ar.noxit.paralleleditor.kernel.basic.SynchronizerFactory;
import ar.noxit.paralleleditor.kernel.basic.UserListMerger;
import ar.noxit.paralleleditor.kernel.basic.sync.SynchronizerAdapterFactory;
import ar.noxit.paralleleditor.kernel.basic.userlist.DefaultUserListMerger;
import ar.noxit.paralleleditor.kernel.remote.KernelService;
import ar.noxit.paralleleditor.kernel.remote.SocketKernelService;

public class ShareManager implements IShareManager {

	private KernelService kernelService;

	private Map<String, JSession> sessions = new HashMap<String, JSession>();

	@Override
	public void createShare(String docTitle, String initialContent) {
		Assert.isNotNull(docTitle);
		Assert.isNotNull(initialContent);

		createServiceIfNotCreated();

		JSession newSession = SessionFactory.newJSession("localhost", 5000, new Documents() {

			@Override
			public void process(CommandFromKernel command) {
			}
		});
		newSession.send(new RemoteLoginRequest("becho"));

		sessions.put(docTitle, newSession);
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
