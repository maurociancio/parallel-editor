package ar.noxit.paralleleditor.common.operations

import ar.noxit.paralleleditor.common.operation.EditOperation

class AddTextOperation(val text: String, val startPos: Int) extends EditOperation {

    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + text + original.substring(startPos)
        println(documentData.data)
    }

    override def toString = {
        "AddTextOperation pos=%d t=%s".format(startPos, text)
    }
}
