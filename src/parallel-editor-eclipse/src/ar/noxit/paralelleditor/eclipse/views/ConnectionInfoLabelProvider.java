package ar.noxit.paralelleditor.eclipse.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public final class ConnectionInfoLabelProvider extends LabelProvider {
	public Image getImage(Object element) {
		return null;
	}

	public String getText(Object element) {
		ConnectionInfo h = (ConnectionInfo) element;
		return element == null ? "" : h.getId().getHost() + ":" + h.getId().getPort() + " as "
				+ h.getUsername();
	}
}