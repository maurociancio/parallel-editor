package ar.noxit.paralleleditor.kernel

trait DocumentHandler {

    def applyChange(operation: EditOperation)
    def installOnUpdateCallback()
}