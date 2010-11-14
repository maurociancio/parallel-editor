package ar.noxit.paralleleditor.common.operation

class NullOperation extends EditOperation {
    def executeOn(documentData: DocumentData) = {}

    override def toString = "NullOperation"

    override def equals(obj: Any) = obj.isInstanceOf[NullOperation]
}
