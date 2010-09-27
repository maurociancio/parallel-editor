package ar.noxit.paralleleditor.kernel.operations

import ar.noxit.paralleleditor.kernel.{DocumentData, EditOperation}

class DeleteTextOperation(val startPos: Int, val size: Int) extends EditOperation {

    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + original.substring(startPos + size)
    }

    override def toString = {
        "DeleteOperation pos=%d s=%d".format(startPos, size)
    }
}
