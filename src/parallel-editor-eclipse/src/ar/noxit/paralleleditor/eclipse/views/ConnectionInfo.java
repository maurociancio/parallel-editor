package ar.noxit.paralleleditor.eclipse.views;

import org.eclipse.core.runtime.Assert;

public class ConnectionInfo {

	private final ConnectionId id;
	private final String username;

	public ConnectionInfo(ConnectionId id, String username) {
		Assert.isNotNull(id);
		Assert.isNotNull(username);

		this.id = id;
		this.username = username;
	}

	public ConnectionId getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}