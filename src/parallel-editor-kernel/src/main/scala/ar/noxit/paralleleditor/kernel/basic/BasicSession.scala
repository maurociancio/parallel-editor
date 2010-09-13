package ar.noxit.paralleleditor.kernel.basic

import ar.noxit.paralleleditor.kernel.Document
import ar.noxit.paralleleditor.kernel.Session

class BasicSession(val username: String) extends Session {
    if (username == null)
        throw new IllegalArgumentException("username cannot be null")
}