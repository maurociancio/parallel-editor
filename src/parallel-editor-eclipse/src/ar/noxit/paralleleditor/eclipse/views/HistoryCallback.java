package ar.noxit.paralleleditor.eclipse.views;

import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;

public class HistoryCallback implements IChatCallback {

	private final Text history;
	private static final String CHAT_MESSAGE = "%s said (from %s): %s";
	private static final String NEW_SUBSCRIBER_MESSAGE = "** New subscriber '%s' on document '%s'.";
	private static final String SUBSCRIBER_LEFT_MESSAGE = "** Subscriber '%s' left document '%s'.";
	private static final String LOGIN_MESSAGE = "** User '%s' has logged in.";
	private static final String LOGOUT_MESSAGE = "** User '%s' has logged out.";

	private static final String LOCAL_CHAT_MESSAGE = "you said (to %s): %s";

	public static String formatLocalChat(Date now, String chat, ConnectionInfo info) {
		return String.format(LOCAL_CHAT_MESSAGE, getStringOf(info), chat) + "\n";
	}

	public HistoryCallback(Text history) {
		this.history = history;
	}

	@Override
	public void onNewChat(Date when, ConnectionInfo from, String username, String chat) {
		append(String.format(CHAT_MESSAGE, username, getStringOf(from), chat));
	}

	@Override
	public void onNewSubscriber(Date when, String username, String docTitle) {
		append(String.format(NEW_SUBSCRIBER_MESSAGE, username, docTitle));
	}

	@Override
	public void onSubscriberLeft(Date when, String username, String docTitle) {
		append(String.format(SUBSCRIBER_LEFT_MESSAGE, username, docTitle));
	}

	@Override
	public void onNewLogin(Date when, String username) {
		append(String.format(LOGIN_MESSAGE, username));
	}

	@Override
	public void onNewLogout(Date date, String username) {
		append(String.format(LOGOUT_MESSAGE, username));
	}

	private static String getStringOf(ConnectionInfo current) {
		return current.getId().getHost() + ":" + current.getId().getPort();
	}

	private void append(final String text) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				history.append(text + "\n");
			}
		});
	}
}