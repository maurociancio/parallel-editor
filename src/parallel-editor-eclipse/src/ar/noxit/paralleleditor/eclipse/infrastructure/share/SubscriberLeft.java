package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import java.util.Date;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;

public class SubscriberLeft implements RemoteEvent {

	private String username;
	private Date when;
	private String docTitle;

	public SubscriberLeft(Date when, String username, String docTitle) {
		this.when = when;
		this.username = username;
		this.docTitle = docTitle;
	}

	@Override
	public void dispatch(IChatCallback callback) {
		callback.onSubscriberLeft(when, username, docTitle);
	}
}
