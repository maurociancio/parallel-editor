package ar.noxit.paralleleditor.kernel

trait Session {

    def installOnUpdateCallback(callback: UpdateCallback)
    def notifyUpdate(message: AnyRef)
    def logout
}