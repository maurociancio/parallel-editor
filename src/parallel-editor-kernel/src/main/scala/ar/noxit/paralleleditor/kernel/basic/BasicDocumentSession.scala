package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.basic.BasicDocument

class BasicDocumentSession(val owner: Session, val document: BasicDocument) extends DocumentSession {

    def applyChange(operation: EditOperation) = {
        operation executeOn document
    }

    def installOnUpdateCallback() = {
    }

    def unsuscribe = {
        document unsuscribe owner
    }
}