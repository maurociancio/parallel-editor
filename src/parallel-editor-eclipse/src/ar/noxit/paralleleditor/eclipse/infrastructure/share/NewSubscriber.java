package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import java.util.Date;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;

public class NewSubscriber implements RemoteEvent {

	private String username;
	private Date when;
	private String docTitle;

	public NewSubscriber(Date when, String username, String docTitle) {
		this.when = when;
		this.username = username;
		this.docTitle = docTitle;
	}

	@Override
	public void dispatch(IChatCallback callback) {
		callback.onNewSubscriber(when, username, docTitle);
	}
}
