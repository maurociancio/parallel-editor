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

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;

import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.ISession.IOnLoginFailureCallback;

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