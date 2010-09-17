package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.UpdateCallback
import ar.noxit.paralleleditor.kernel.Document
import ar.noxit.paralleleditor.kernel.Session

class BasicSession(val username: String, val kernel: BasicKernel) extends Session {

    var updateCallback: UpdateCallback = _

    if (username == null)
        throw new IllegalArgumentException("username cannot be null")

    def installOnUpdateCallback(callback: UpdateCallback) = {
        this updateCallback = callback
    }

    def logout = {
        kernel logout this
    }
}