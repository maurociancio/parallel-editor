package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.messages.SyncOperation

trait MessageConverter {
    def convert(syncOperation: SyncOperation): Message[EditOperation]
}

class DefaultMessageConverter(val remoteOpConverter: RemoteOperationConverter) extends MessageConverter {
    override def convert(syncOperation: SyncOperation) = {
        val op = remoteOpConverter.convert(syncOperation.payload)
        val syncStatus = syncOperation.syncSatus
        Message(op, syncStatus.myMsgs, syncStatus.otherMessages)
    }
}
