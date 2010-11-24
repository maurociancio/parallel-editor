package ar.noxit.paralleleditor.eclipse.infrastructure.share.manager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IOnLoginFailureCallback;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public class LoginFailureCallback implements IOnLoginFailureCallback {

	private final ConnectionInfo info;
	private final ShareManager shareManager;
	private IOnLoginFailureCallback callback;
	private boolean loginFailureReceived = false;

	public LoginFailureCallback(ConnectionInfo info, ShareManager shareManager) {
		this.info = info;
		this.shareManager = shareManager;
	}

	@Override
	public synchronized void onLoginFailure() {
		final IOnLoginFailureCallback callbackToInvoke = callback;

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				shareManager.disconnect(info.getId());

				if (callbackToInvoke != null)
					callbackToInvoke.onLoginFailure();
				else
					loginFailureReceived = true;
			}
		});
	}

	public synchronized void setCallback(IOnLoginFailureCallback callback) {
		Assert.isNotNull(callback);

		if (this.callback == null && loginFailureReceived) {
			callback.onLoginFailure();
			loginFailureReceived = false;
		}

		this.callback = callback;
	}
}