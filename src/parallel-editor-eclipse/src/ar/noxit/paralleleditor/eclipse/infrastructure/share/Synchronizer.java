package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;

import ar.noxit.paralleleditor.common.ApplyFunction;
import ar.noxit.paralleleditor.common.BasicXFormStrategy;
import ar.noxit.paralleleditor.common.EditOperationJupiterSynchronizer;
import ar.noxit.paralleleditor.common.JEditOperationJupiterSynchronizer;
import ar.noxit.paralleleditor.common.Message;
import ar.noxit.paralleleditor.common.SendFunction;
import ar.noxit.paralleleditor.common.operation.DocumentData;
import ar.noxit.paralleleditor.common.operation.EditOperation;

public class Synchronizer implements IOperationCallback, IRemoteMessageCallback {

	private final JEditOperationJupiterSynchronizer sync = new JEditOperationJupiterSynchronizer(
			new EditOperationJupiterSynchronizer(new BasicXFormStrategy()));

	private final IDocumentSession docSession;
	private final DocumentData documentData;

	public Synchronizer(IDocumentSession docSession, DocumentData documentData) {
		Assert.isNotNull(docSession);
		Assert.isNotNull(documentData);

		this.docSession = docSession;
		this.documentData = documentData;
	}

	@Override
	public void apply(EditOperation editOperation) {
		sync.generate(editOperation, new SendFunction() {

			@Override
			public void send(Message<EditOperation> message) {
				docSession.onNewLocalMessage(message);
			}
		});
	}

	@Override
	public void onNewRemoteMessage(final Message<EditOperation> message) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				sync.receive(message, new ApplyFunction() {

					@Override
					public void apply(EditOperation editOperation) {
						editOperation.executeOn(documentData);
					}
				});
			}
		});
	}
}