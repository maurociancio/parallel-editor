package ar.noxit.paralleleditor.common.operation

class DeleteTextOperation(val startPos: Int, val size: Int) extends EditOperation {
    def executeOn(documentData: DocumentData) = {
        // caret position
        val caret = documentData.caret
        val caretOffset = caret.offset
        val selectionLength = caret.selectionLength

        // data
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + original.substring(startPos + size)

        // move the caret
        val selectedRange = caretOffset to (caretOffset + selectionLength)
        val deletionRange = startPos to (startPos + size)

        val intersection = selectedRange intersect deletionRange

        if (intersection.size <= 1) {
            // caso 1 o 2
            if (selectedRange.last <= deletionRange.head) {
                // caso 1
                caret.change(caretOffset, selectionLength)
            } else {
                // caso 2
                caret.change(caretOffset - size, selectionLength)
            }
        } else {
            // caso 3, 4, 5 o 6

            // caso 4
            if (intersection == selectedRange) {
                caret.change(startPos, 0)
            } else if (intersection == deletionRange) {
                // caso 6
                caret.change(caretOffset, selectionLength - intersection.size + 1)
            } else {
                // caso 3
                if (startPos < caretOffset) {
                    caret.change(startPos, intersection.size - 1)
                } else {
                    //  caso 5
                    caret.change(caretOffset, intersection.size - 1)
                }
            }
        }
    }

    override def toString = "DeleteOperation pos=%d s=%d".format(startPos, size)

    override def equals(obj: Any) =
        obj match {
            case other: DeleteTextOperation =>
                startPos == other.startPos && size == other.size
            case _ => false
        }
}
