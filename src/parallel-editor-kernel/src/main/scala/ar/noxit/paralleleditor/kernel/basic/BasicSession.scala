package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.UpdateCallback
import ar.noxit.paralleleditor.kernel.Session

class BasicSession(val username: String, private val kernel: BasicKernel) extends Session {
    var updateCallback: UpdateCallback = _

    if (username == null)
        throw new IllegalArgumentException("username cannot be null")

    override def installOnUpdateCallback(callback: UpdateCallback) = this updateCallback = callback

    override def notifyUpdate(message: AnyRef) = {
        if (updateCallback != null)
            updateCallback update message
    }

    override def logout = kernel logout this
}
