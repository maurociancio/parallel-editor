package ar.noxit.paralleleditor.eclipse.share.events;

import java.util.Date;

import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;

public class NewLogout implements RemoteEvent {

	private Date when;
	private String username;

	public NewLogout(Date when, String username) {
		this.when = when;
		this.username = username;
	}

	@Override
	public void dispatch(IChatCallback callback) {
		callback.onNewLogout(when, username);
	}
}
