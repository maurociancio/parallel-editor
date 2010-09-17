package ar.noxit.paralleleditor.kernel

trait Session {

    def installOnUpdateCallback(callback: UpdateCallback)
    def logout
}