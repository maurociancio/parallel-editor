package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.{CompositeRemoteOperation, RemoteDeleteText, RemoteAddText, RemoteOperation}
import ar.noxit.paralleleditor.common.operation.{CompositeOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait EditOperationConverter {
    def convert(docTitle: String, e: EditOperation): RemoteOperation
}

class DefaultMessageConverter extends EditOperationConverter {
    override def convert(docTitle: String, e: EditOperation): RemoteOperation = {
        e match {
            case at: AddTextOperation => RemoteAddText(docTitle, at.text, at.startPos)
            case rt: DeleteTextOperation => RemoteDeleteText(docTitle, rt.startPos, rt.size)
            case c: CompositeOperation => CompositeRemoteOperation(docTitle, c.ops.map {op => convert(docTitle, op)}: _*)
        }
    }
}
