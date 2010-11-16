package ar.noxit.paralleleditor.common.operation

trait EditOperation {
    def executeOn(documentData: DocumentData)
}
