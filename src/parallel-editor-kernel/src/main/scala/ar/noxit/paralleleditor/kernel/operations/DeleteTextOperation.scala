package ar.noxit.paralleleditor.kernel.operations

import ar.noxit.paralleleditor.kernel.{Session, DocumentData, EditOperation}

class DeleteTextOperation(val startPos: Int, val size: Int) extends EditOperation {

    var source: Session = _

    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + original.substring(startPos + size)
        println(documentData.data)
    }

    override def toString = {
        "DeleteOperation pos=%d s=%d".format(startPos, size)
    }
}
