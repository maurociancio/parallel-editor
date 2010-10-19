package ar.noxit.paralleleditor.common.converter

import ar.noxit.paralleleditor.common.messages.RemoteDocumentOperation
import ar.noxit.paralleleditor.common.operation.DocumentOperation

trait RemoteDocumentOperationConverter {
    def convert(docOp: DocumentOperation): RemoteDocumentOperation
}

class DefaultRemoteDocumentOperationConverter(val syncOpConverter: SyncOperationConverter) extends RemoteDocumentOperationConverter {
    override def convert(docOp: DocumentOperation) = {
        RemoteDocumentOperation(docOp.docTitle, syncOpConverter.convert(docOp.message))
    }
}
