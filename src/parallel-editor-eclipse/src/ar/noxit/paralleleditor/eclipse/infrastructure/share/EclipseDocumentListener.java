package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.common.operation.EditOperation;

public class EclipseDocumentListener extends BaseEclipseDocumentListener {

	private final IOperationCallback operationCallback;

	public EclipseDocumentListener(IOperationCallback operationCallback) {
		Assert.isNotNull(operationCallback);
		this.operationCallback = operationCallback;
	}

	@Override
	protected void processOperation(EditOperation editOperation) {
		operationCallback.apply(editOperation);
	}
}
