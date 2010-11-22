package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.UpdateCallback
import ar.noxit.paralleleditor.kernel.Session

class BasicSession(val username: String, private val kernel: BasicKernel) extends Session {
    private var updateCallback: UpdateCallback = _

    if (username == null)
        throw new IllegalArgumentException("username cannot be null")

    override def installOnUpdateCallback(callback: UpdateCallback) = this updateCallback = callback

    override def notifyUpdate(message: Any) = {
        if (updateCallback != null)
            updateCallback update message
    }

    //TODO comparar tambien el kernel
    override def equals(obj: Any) = {
        obj.isInstanceOf[Session] && obj.asInstanceOf[Session].username == username
    }

    override def logout = kernel logout this
}
