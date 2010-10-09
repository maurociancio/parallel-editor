package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.common.operation.EditOperation

trait DocumentSession {

    val title: String
    def applyChange(operation: EditOperation)
    def unsubscribe
}
