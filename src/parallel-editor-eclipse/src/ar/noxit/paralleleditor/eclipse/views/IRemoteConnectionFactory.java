package ar.noxit.paralleleditor.eclipse.views;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;
import ar.noxit.paralleleditor.eclipse.views.ConnectionInfo.ConnectionId;

public interface IRemoteConnectionFactory {

	ISession connect(ConnectionInfo info);

	ISession getSession(ConnectionId id);

	ConnectionStatus statusOf(ConnectionId id);
}
