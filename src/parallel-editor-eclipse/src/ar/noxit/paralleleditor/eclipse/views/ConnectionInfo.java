package ar.noxit.paralleleditor.eclipse.views;

public class ConnectionInfo {

	private final ConnectionId id;
	private final String username;
	private final boolean isLocal;

	public ConnectionInfo(ConnectionId id, String username) {
		this(id, username, false);
	}

	public ConnectionInfo(ConnectionId id, String username, boolean isLocal) {
		this.id = id;
		this.username = username;
		this.isLocal = isLocal;
	}

	public ConnectionId getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public boolean isLocal() {
		return isLocal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isLocal ? 1231 : 1237);
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionInfo other = (ConnectionInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isLocal != other.isLocal)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}