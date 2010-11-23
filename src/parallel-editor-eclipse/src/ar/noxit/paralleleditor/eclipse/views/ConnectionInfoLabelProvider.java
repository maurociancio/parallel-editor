package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.jface.viewers.LabelProvider;

public class ConnectionInfoLabelProvider extends LabelProvider {

	private boolean showLocalMark = true;

	public ConnectionInfoLabelProvider() {
	}

	public ConnectionInfoLabelProvider(boolean showLocalMark) {
		this.showLocalMark = showLocalMark;
	}

	public String getText(Object element) {
		ConnectionInfo h = (ConnectionInfo) element;
		if (element == null)
			return null;

		ConnectionId id = h.getId();
		String isLocal = id.isLocal() && showLocalMark ? "[X]" : "";
		return String.format("%s %s:%d as %s", isLocal, id.getHost(), id.getPort(), h.getUsername());
	}
}