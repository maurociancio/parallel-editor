package ar.noxit.paralleleditor.eclipse.share.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;
import ar.noxit.paralleleditor.eclipse.share.events.ChatMessage;
import ar.noxit.paralleleditor.eclipse.share.events.NewLogin;
import ar.noxit.paralleleditor.eclipse.share.events.NewLogout;
import ar.noxit.paralleleditor.eclipse.share.events.NewSubscriber;
import ar.noxit.paralleleditor.eclipse.share.events.RemoteEvent;
import ar.noxit.paralleleditor.eclipse.share.events.SubscriberLeft;

public class ChatCallbackAdapter implements IChatCallback {

	private IChatCallback adapted;
	private final List<RemoteEvent> holdedMessages = new ArrayList<RemoteEvent>();

	@Override
	public synchronized void onNewChat(Date when, ConnectionInfo from, String username, String message) {
		addOrDispatch(new ChatMessage(when, from, username, message));
	}

	@Override
	public void onNewSubscriber(Date when, String username, String docTitle) {
		addOrDispatch(new NewSubscriber(when, username, docTitle));
	}

	@Override
	public void onSubscriberLeft(Date when, String username, String docTitle) {
		addOrDispatch(new SubscriberLeft(when, username, docTitle));
	}

	@Override
	public void onNewLogin(Date when, String username) {
		addOrDispatch(new NewLogin(when, username));
	}

	@Override
	public void onNewLogout(Date when, String username) {
		addOrDispatch(new NewLogout(when, username));
	}

	private synchronized void addOrDispatch(RemoteEvent event) {
		if (adapted != null)
			event.dispatch(adapted);
		else
			holdedMessages.add(event);
	}

	private synchronized void dispatchHolded(IChatCallback adapted) {
		for (RemoteEvent m : holdedMessages) {
			m.dispatch(adapted);
		}
		holdedMessages.clear();
	}

	public synchronized void setAdapted(IChatCallback adapted) {
		if (this.adapted == null && adapted != null)
			dispatchHolded(adapted);
		this.adapted = adapted;
	}
}
