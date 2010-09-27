package ar.noxit.paralleleditor.kernel

trait EditOperation {

    var source: Session
    def executeOn(documentData: DocumentData)
}
