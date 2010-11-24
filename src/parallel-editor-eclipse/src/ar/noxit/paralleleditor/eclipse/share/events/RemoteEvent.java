package ar.noxit.paralleleditor.eclipse.share.events;

import ar.noxit.paralleleditor.eclipse.share.ISession.IChatCallback;

public interface RemoteEvent {
	void dispatch(IChatCallback callback);
}