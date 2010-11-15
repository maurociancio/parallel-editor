package ar.noxit.paralelleditor.eclipse.views;

public interface IRemoteConnectionFactory {

	void connect(ConnectionInfo info);

	ConnectionStatus statusOf(ConnectionInfo info);
}
