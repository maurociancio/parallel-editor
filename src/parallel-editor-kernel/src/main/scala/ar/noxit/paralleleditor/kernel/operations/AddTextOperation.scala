package ar.noxit.paralleleditor.kernel.operations

import ar.noxit.paralleleditor.kernel.Document
import ar.noxit.paralleleditor.kernel.EditOperation

class AddTextOperation(val text: String, val startPos: Long) extends EditOperation {

    def executeOn(document: Document) = {
    }
}