package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.UpdateCallback
import ar.noxit.paralleleditor.kernel.Document
import ar.noxit.paralleleditor.kernel.Session

class BasicSession(val username: String, val kernel: BasicKernel) extends Session {

    var updateCallback: UpdateCallback = _

    if (username == null)
        throw new IllegalArgumentException("username cannot be null")

    override def installOnUpdateCallback(callback: UpdateCallback) = this updateCallback = callback

    override def notifyUpdate(message: AnyRef) = updateCallback update message

    override def logout = kernel logout this
}