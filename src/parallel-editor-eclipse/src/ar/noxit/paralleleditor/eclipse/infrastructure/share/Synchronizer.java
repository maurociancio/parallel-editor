package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.EditOperationJupiterSynchronizer;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public class Synchronizer implements IOperationCallback, IRemoteMessageCallback {

	private final EditOperationJupiterSynchronizer sync = new EditOperationJupiterSynchronizer(new BasicXFormStrategy());
	private final IDocumentSession docSession;

	public Synchronizer(IDocumentSession docSession) {
		Assert.isNotNull(docSession);

		this.docSession = docSession;
	}

	@Override
	public void apply(EditOperation editOperation) {
		// TODO aplicar la operacion y enviarla
		docSession.onNewLocalMessage(null);
	}

	@Override
	public void onNewRemoteMessage(Message<EditOperation> message) {
		// TODO aplicar mensaje y luego en editor
	}
}