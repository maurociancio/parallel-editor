package ar.noxit.paralleleditor.eclipse.infrastructure.share;

import ar.noxit.paralleleditor.common.operation.EditOperation;

public interface IOperationCallback {

	void apply(EditOperation editOperation);
}
