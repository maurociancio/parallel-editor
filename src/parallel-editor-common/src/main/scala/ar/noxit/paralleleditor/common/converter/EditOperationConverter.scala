package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.operation.{CompositeOperation, DeleteTextOperation, AddTextOperation, EditOperation}
import ar.noxit.paralleleditor.common.messages._

trait EditOperationConverter {
    def convert(docTitle: String, myMsgs: Int, otherMsgs: Int, e: EditOperation): RemoteOperation
}

class DefaultMessageConverter extends EditOperationConverter {
    override def convert(docTitle: String, myMsgs: Int, otherMsgs: Int, e: EditOperation): RemoteOperation = {
        val s = SyncStatus(myMsgs, otherMsgs)
        e match {
            case at: AddTextOperation => RemoteAddText(docTitle, s, at.text, at.startPos, at.pword)
            case rt: DeleteTextOperation => RemoteDeleteText(docTitle, s, rt.startPos, rt.size)
            case c: CompositeOperation => CompositeRemoteOperation(docTitle, s, c.ops.map {op => convert(docTitle, myMsgs, otherMsgs, op)}: _*)
        }
    }
}
