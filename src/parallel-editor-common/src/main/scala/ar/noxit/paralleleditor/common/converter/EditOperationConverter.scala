package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages._
import ar.noxit.paralleleditor.common.operation.{NullOperation, DeleteTextOperation, AddTextOperation, EditOperation}

trait EditOperationConverter {
    def convert(e: EditOperation): RemoteOperation
}

class DefaultEditOperationConverter extends EditOperationConverter {
    override def convert(e: EditOperation) = {
        e match {
            case at: AddTextOperation => RemoteAddText(at.text, at.startPos, at.pword)
            case rt: DeleteTextOperation => RemoteDeleteText(rt.startPos, rt.size)
            case nuop: NullOperation => RemoteNullOpText()
        }
    }
}
