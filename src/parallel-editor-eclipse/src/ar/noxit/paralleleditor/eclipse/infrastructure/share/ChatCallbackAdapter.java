package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public class ChatCallbackAdapter implements IChatCallback {

	private IChatCallback adapted;

	@Override
	public synchronized void onNewChat(ConnectionInfo from, String username, String message) {
		if (adapted != null)
			adapted.onNewChat(from, username, message);
	}

	public synchronized void setAdapted(IChatCallback adapted) {
		this.adapted = adapted;
	}
}
