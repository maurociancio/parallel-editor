package ar.noxit.paralleleditor.kernel.operations

import ar.noxit.paralleleditor.kernel.{Session, DocumentData, EditOperation}

class AddTextOperation(val text: String, val startPos: Int) extends EditOperation {

    var source: Session = _

    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + text + original.substring(startPos)
    }

    override def toString = {
        "AddTextOperation pos=%d t=%s".format(startPos, text)
    }
}
