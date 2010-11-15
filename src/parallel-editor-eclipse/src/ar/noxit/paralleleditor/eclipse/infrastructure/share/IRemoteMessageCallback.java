package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public interface IRemoteMessageCallback {

	void onNewRemoteMessage(Message<EditOperation> message);
}