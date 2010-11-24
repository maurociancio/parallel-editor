package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import java.util.Date;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;

public class NewLogin implements RemoteEvent {

	private String username;
	private Date when;

	public NewLogin(Date when, String username) {
		this.when = when;
		this.username = username;
	}

	@Override
	public void dispatch(IChatCallback callback) {
		callback.onNewLogin(when, username);
	}
}
