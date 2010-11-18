package ar.noxit.paralelleditor.eclipse.views;

import ar.noxit.paralelleditor.eclipse.views.ConnectionInfo.ConnectionId;
import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;

public interface IRemoteConnectionFactory {

	ISession connect(ConnectionInfo info);

	ISession getSession(ConnectionId id);

	ConnectionStatus statusOf(ConnectionId id);
}
