package ar.noxit.paralleleditor.eclipse.views;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;

public interface IRemoteConnectionFactory {

	ISession connect(ConnectionInfo info);

	ISession getSession(ConnectionId id);

	ConnectionStatus statusOf(ConnectionId id);
}
