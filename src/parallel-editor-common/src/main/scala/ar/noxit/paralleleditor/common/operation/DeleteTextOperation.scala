package ar.noxit.paralleleditor.common.operation

class DeleteTextOperation(val startPos: Int, val size: Int) extends EditOperation {
    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + original.substring(startPos + size)
    }

    override def toString = "DeleteOperation pos=%d s=%d".format(startPos, size)

    override def equals(obj: Any) =
        obj match {
            case other: DeleteTextOperation =>
                startPos == other.startPos && size == other.size
            case _ => false
        }
}
