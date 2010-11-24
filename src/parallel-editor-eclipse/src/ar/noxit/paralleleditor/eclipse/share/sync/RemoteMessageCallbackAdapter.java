package ar.noxit.paralleleditor.eclipse.share.sync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public final class RemoteMessageCallbackAdapter implements IRemoteMessageCallback {

	private IRemoteMessageCallback adapted = null;
	private List<Message<EditOperation>> queued = new ArrayList<Message<EditOperation>>();

	@Override
	public synchronized void onNewRemoteMessage(Message<EditOperation> message) {
		if (adapted == null) {
			queued.add(message);
		} else {
			adapted.onNewRemoteMessage(message);
		}
	}

	public synchronized void setAdapted(IRemoteMessageCallback adapted) {
		Assert.isNotNull(adapted);

		if (this.adapted == null) {
			Iterator<Message<EditOperation>> it = queued.iterator();
			while (it.hasNext()) {
				adapted.onNewRemoteMessage(it.next());
				it.remove();
			}
		}

		this.adapted = adapted;
	}
}