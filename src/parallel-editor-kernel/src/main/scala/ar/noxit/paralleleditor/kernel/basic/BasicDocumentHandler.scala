package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.basic.BasicDocument

class BasicDocumentHandler(val owner: Session, val document: BasicDocument) extends DocumentHandler {

    def applyChange(operation: EditOperation) = {
        operation executeOn document
    }

    def installOnUpdateCallback() = {
    }

    def unsuscribe = {
        document unsuscribe owner
    }
}