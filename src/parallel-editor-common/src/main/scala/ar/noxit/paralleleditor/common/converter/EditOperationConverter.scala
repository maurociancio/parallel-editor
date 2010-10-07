package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.operation.{DeleteTextOperation, AddTextOperation, EditOperation}
import ar.noxit.paralleleditor.common.messages.{RemoteDeleteText, RemoteAddText, RemoteOperation}

trait EditOperationConverter {
    def convert(e: EditOperation): RemoteOperation
}

class DefaultMessageConverter extends EditOperationConverter {
    override def convert(e: EditOperation) = {
        e match {
            case at: AddTextOperation => RemoteAddText(at.text, at.startPos)
            case rt: DeleteTextOperation => RemoteDeleteText(rt.startPos, rt.size)
        }
    }
}
