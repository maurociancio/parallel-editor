package ar.noxit.paralleleditor.common.operation

class CompositeOperation(val ops: EditOperation*) extends EditOperation {
    def executeOn(documentData: DocumentData) = ops.foreach(_.executeOn(documentData))
}
