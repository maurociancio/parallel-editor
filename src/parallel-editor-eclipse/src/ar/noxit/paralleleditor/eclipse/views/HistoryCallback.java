package ar.noxit.paralleleditor.eclipse.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;

public class HistoryCallback implements IChatCallback {

	private final Text history;
	private static final String CHAT_MESSAGE = "%s %s said (from %s): %s";
	private static final String NEW_SUBSCRIBER_MESSAGE = "** %s New subscriber '%s' on document '%s'.";
	private static final String SUBSCRIBER_LEFT_MESSAGE = "** %s Subscriber '%s' left document '%s'.";
	private static final String LOGIN_MESSAGE = "** %s User '%s' has logged in.";
	private static final String LOGOUT_MESSAGE = "** %s User '%s' has logged out.";

	private static final String LOCAL_CHAT_MESSAGE = "%s you said (to %s): %s";

	public static String formatLocalChat(Date now, String chat, ConnectionInfo info) {
		return "\n" + String.format(LOCAL_CHAT_MESSAGE, formatTime(now), getStringOf(info), chat);
	}

	public HistoryCallback(Text history) {
		this.history = history;
	}

	@Override
	public void onNewChat(Date when, ConnectionInfo from, String username, String chat) {
		append(String.format(CHAT_MESSAGE, formatTime(when), username, getStringOf(from), chat));
	}

	@Override
	public void onNewSubscriber(Date when, String username, String docTitle) {
		append(String.format(NEW_SUBSCRIBER_MESSAGE, formatTime(when), username, docTitle));
	}

	@Override
	public void onSubscriberLeft(Date when, String username, String docTitle) {
		append(String.format(SUBSCRIBER_LEFT_MESSAGE, formatTime(when), username, docTitle));
	}

	@Override
	public void onNewLogin(Date when, String username) {
		append(String.format(LOGIN_MESSAGE, formatTime(when), username));
	}

	@Override
	public void onNewLogout(Date date, String username) {
		append(String.format(LOGOUT_MESSAGE, formatTime(date), username));
	}

	private static String getStringOf(ConnectionInfo current) {
		return current.getId().getHost() + ":" + current.getId().getPort();
	}

	private static String formatTime(Date date) {
		return "[" + new SimpleDateFormat("HH:mm:ss").format(date) + "]";
	}

	private void append(final String text) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				history.append("\n" + text);
			}
		});
	}
}