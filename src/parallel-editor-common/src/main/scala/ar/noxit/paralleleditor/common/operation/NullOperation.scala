package ar.noxit.paralleleditor.common.operation

class NullOperation(val operation: EditOperation) extends EditOperation {
    def this() {
        this (null)
    }

    def executeOn(documentData: DocumentData) = {
        if (operation != null)
            operation.executeOn(documentData)
    }
}
