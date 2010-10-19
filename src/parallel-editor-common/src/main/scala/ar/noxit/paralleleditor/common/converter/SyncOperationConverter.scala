package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.Message
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.messages.{SyncStatus, SyncOperation}

trait SyncOperationConverter {
    def convert(message: Message[EditOperation]): SyncOperation
}

class DefaultSyncOperationConverter(val editOpConverter: EditOperationConverter) extends SyncOperationConverter {
    def convert(message: Message[EditOperation]) = {
        val converted = editOpConverter.convert(message.op)
        SyncOperation(SyncStatus(message.myMsgs, message.otherMsgs), converted)
    }
}
