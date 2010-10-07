package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{CompositeRemoteOperation, RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{CompositeOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait EditOperationConverter {
    def convert(e: EditOperation): RemoteOperation
}

class DefaultMessageConverter extends EditOperationConverter {
    override def convert(e: EditOperation): RemoteOperation = {
        e match {
            case at: AddTextOperation => RemoteAddText(at.text, at.startPos)
            case rt: DeleteTextOperation => RemoteDeleteText(rt.startPos, rt.size)
            case c: CompositeOperation => CompositeRemoteOperation(c.ops.map {convert _}: _*)
        }
    }
}
