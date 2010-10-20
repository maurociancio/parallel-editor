package ar.noxit.paralleleditor.kernel

trait Session {

    val username: String
    def installOnUpdateCallback(callback: UpdateCallback)
    def notifyUpdate(message: AnyRef)
    def logout
}
