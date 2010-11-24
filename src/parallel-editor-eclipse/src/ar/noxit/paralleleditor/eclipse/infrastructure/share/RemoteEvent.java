package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import ar.noxit.paralleleditor.eclipse.infrastructure.share.manager.ISession.IChatCallback;

public interface RemoteEvent {
	void dispatch(IChatCallback callback);
}