package ar.noxit.paralleleditor.client

import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.Message

trait ConcurrentDocument {
    def processRemoteOperation(m: Message[EditOperation])
}