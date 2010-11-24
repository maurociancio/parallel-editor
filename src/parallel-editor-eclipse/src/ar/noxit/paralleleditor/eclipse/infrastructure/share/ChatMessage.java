package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import java.util.Date;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo;

public class ChatMessage implements RemoteEvent {
	private ConnectionInfo from;
	private String username;
	private String message;
	private Date when;

	public ChatMessage(Date when, ConnectionInfo from, String username, String message) {
		this.when = when;
		this.from = from;
		this.username = username;
		this.message = message;
	}

	@Override
	public void dispatch(IChatCallback callback) {
		callback.onNewChat(when, from, username, message);
	}
}