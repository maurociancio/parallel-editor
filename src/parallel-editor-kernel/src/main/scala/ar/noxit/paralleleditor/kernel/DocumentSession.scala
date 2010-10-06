package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.common.operation.EditOperation

trait DocumentSession {

    def applyChange(operation: EditOperation)
    def unsubscribe
}
