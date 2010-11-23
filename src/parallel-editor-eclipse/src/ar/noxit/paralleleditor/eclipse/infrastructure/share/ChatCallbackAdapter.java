package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import java.util.ArrayList;
import java.util.List;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public class ChatCallbackAdapter implements IChatCallback {

	private IChatCallback adapted;
	private final List<ChatMessage> holdedMessages = new ArrayList<ChatMessage>();

	@Override
	public synchronized void onNewChat(ConnectionInfo from, String username, String message) {
		if (adapted != null)
			adapted.onNewChat(from, username, message);
		else
			holdedMessages.add(new ChatMessage(from, username, message));
	}

	public synchronized void setAdapted(IChatCallback adapted) {
		if (this.adapted == null && adapted != null)
			dispatch(adapted);
		this.adapted = adapted;
	}

	private synchronized void dispatch(IChatCallback adapted) {
		for (ChatMessage m : holdedMessages) {
			adapted.onNewChat(m.from, m.username, m.message);
		}
		holdedMessages.clear();
	}

	public static class ChatMessage {
		private ConnectionInfo from;
		private String username;
		private String message;

		public ChatMessage(ConnectionInfo from, String username, String message) {
			this.from = from;
			this.username = username;
			this.message = message;
		}
	}
}
