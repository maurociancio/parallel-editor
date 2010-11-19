package ar.noxit.paralleleditor.eclipse.views;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;

public interface IRemoteConnectionFactory {

	ISession connect(ConnectionInfo info);

	void disconnect(ConnectionId id);

	ISession getSession(ConnectionId id);

	boolean isConnected(ConnectionId connectionId);

	ConnectionStatus statusOf(ConnectionId id);

	void stopLocalService();
}
