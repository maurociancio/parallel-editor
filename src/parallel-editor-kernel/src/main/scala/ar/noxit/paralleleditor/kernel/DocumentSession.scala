package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation

trait DocumentSession {

    val title: String
    def applyChange(msg: Message[EditOperation])
    def unsubscribe
}
