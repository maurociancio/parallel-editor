package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{CompositeRemoteOperation, RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{CompositeOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation
}

class DefaultRemoteOperationConverter extends RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation = {
        o match {
            case RemoteAddText(text, startPos) => new AddTextOperation(text, startPos)
            case RemoteDeleteText(text, startPos) => new DeleteTextOperation(text, startPos)
            case c: CompositeRemoteOperation => new CompositeOperation(c.ops.map {convert _}: _*)
        }
    }
}
