package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{DeleteTextOperation, AddTextOperation, EditOperation}

trait RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation
}

class DefaultRemoteOperationConverter extends RemoteOperationConverter {
    def convert(o: RemoteOperation) = {
        o match {
            case RemoteAddText(text, startPos) => new AddTextOperation(text, startPos)
            case RemoteDeleteText(text, startPos) => new DeleteTextOperation(text, startPos)
        }
    }
}
