package ar.noxit.paralelleditor.eclipse.views;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession;

public interface IRemoteConnectionFactory {

	ISession connect(ConnectionInfo info);

	ConnectionStatus statusOf(ConnectionInfo info);
}
