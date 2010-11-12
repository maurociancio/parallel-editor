package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.IShareManager;
import ar.noxit.paralleleditor.kernel.remote.KernelService;
import ar.noxit.paralleleditor.kernel.remote.SocketKernelService;

public class ShareManager implements IShareManager {

	private KernelService kernelService;

	@Override
	public void createShare(String docTitle) {
		Assert.isNotNull(docTitle);
	}

	protected KernelService createOrGetKernelService() {
		if (kernelService == null) {
			kernelService = new SocketKernelService(5000);
			kernelService.startService();
		}
		return kernelService;
	}
}
