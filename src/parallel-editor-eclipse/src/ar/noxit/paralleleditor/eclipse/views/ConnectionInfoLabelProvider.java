package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.jface.viewers.LabelProvider;

public class ConnectionInfoLabelProvider extends LabelProvider {

	public String getText(Object element) {
		ConnectionInfo h = (ConnectionInfo) element;
		return element == null ? "" : h.getId().getHost() + ":" + h.getId().getPort() + " as "
				+ h.getUsername();
	}
}