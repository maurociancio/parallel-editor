package ar.noxit.paralleleditor.eclipse.editor.listeners;

import org.eclipse.core.runtime.Assert;

import ar.noxit.paralleleditor.common.operation.EditOperation;
import ar.noxit.paralleleditor.eclipse.share.sync.IOperationCallback;

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
