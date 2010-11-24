package ar.noxit.paralleleditor.eclipse.share.sync;

import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public interface IDocumentSession {

	void onNewLocalMessage(Message<EditOperation> message);

	void installCallback(IRemoteMessageCallback remoteCallback);

	void unsubscribe();
}
