package ar.noxit.paralleleditor.kernel.operations

import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.operations.DocumentData

class GetTextOperation extends EditOperation {

    var text: String = _

    def executeOn(documentData: DocumentData) = {
        text = documentData.data
    }

    override def toString = {
        "GetTextOperation"
    }
}
