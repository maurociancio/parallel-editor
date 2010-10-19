package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{RemoteNullOpText, RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{NullOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation
}

class DefaultRemoteOperationConverter extends RemoteOperationConverter {
    def convert(o: RemoteOperation) = {
        o match {
            case at: RemoteAddText => new AddTextOperation(at.text, at.startPos, at.pword)
            case dt: RemoteDeleteText => new DeleteTextOperation(dt.startPos, dt.size)
            case n: RemoteNullOpText => new NullOperation
        }
    }
}
