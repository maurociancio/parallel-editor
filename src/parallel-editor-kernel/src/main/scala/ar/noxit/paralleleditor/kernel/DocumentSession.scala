package ar.noxit.paralleleditor.kernel

trait DocumentSession {

    def applyChange(operation: EditOperation)
    def installOnUpdateCallback
    def unsuscribe
}