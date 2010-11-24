package ar.noxit.paralleleditor.eclipse.share.events;

import java.util.Date;

import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;

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
