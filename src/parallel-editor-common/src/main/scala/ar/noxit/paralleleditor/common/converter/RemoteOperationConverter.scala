package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{CompositeRemoteOperation, RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{CompositeOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation
}

class DefaultRemoteOperationConverter extends RemoteOperationConverter {
    def convert(o: RemoteOperation): EditOperation = {
        o match {
            case at: RemoteAddText => new AddTextOperation(at.text, at.startPos)
            case dt: RemoteDeleteText => new DeleteTextOperation(dt.startPos, dt.size)
            case c: CompositeRemoteOperation => new CompositeOperation(c.ops.map {convert _}: _*)
        }
    }
}
