package ar.noxit.paralleleditor.eclipse.views.share;

import ar.noxit.paralleleditor.eclipse.model.ConnectionId;
import ar.noxit.paralleleditor.eclipse.model.ConnectionInfo;
import ar.noxit.paralleleditor.eclipse.model.ConnectionStatus;
import ar.noxit.paralleleditor.eclipse.share.ISession;

public interface IConnectionFactory {

	ISession connect(ConnectionInfo info);

	void disconnect(ConnectionId id);

	ISession getSession(ConnectionId id);

	boolean isConnected(ConnectionId connectionId);

	ConnectionStatus statusOf(ConnectionId id);

	void stopLocalService();
}
