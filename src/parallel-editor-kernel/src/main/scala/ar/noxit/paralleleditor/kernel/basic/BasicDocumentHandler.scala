package ar.noxit.paralleleditor.kernel

class BasicDocumentHandler(owner: Session, val document: Document) extends DocumentHandler {
    document.suscribe(owner)

    def applyChange(operation: EditOperation) = {
        operation executeOn document
    }

    def installOnUpdateCallback() = {
    }
}