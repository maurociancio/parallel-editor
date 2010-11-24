package ar.noxit.paralleleditor.eclipse.share.sync;

import ar.noxit.paralleleditor.common.operation.EditOperation;

public interface IOperationCallback {

	void apply(EditOperation editOperation);
}
