package ar.noxit.paralleleditor.common.operation

class AddTextOperation(val text: String, val startPos: Int, val pword: List[Int] = List()) extends EditOperation {
    def executeOn(documentData: DocumentData) = {
        // caret position
        val caret = documentData.caret
        val caretOffset = caret.offset
        val selectionLength = caret.selectionLength

        val original = documentData.data
        // insert the text
        documentData.data = original.substring(0, startPos) + text + original.substring(startPos)

        // selection range
        val selectedRange = (caretOffset + 1) until (caretOffset + selectionLength)
        if (selectedRange contains startPos) {
            caret.change(caretOffset, selectionLength + text.size)
        } else {
            if (caretOffset < startPos)
                caret.change(caretOffset, selectionLength)
            else
                caret.change(caretOffset + text.size, selectionLength)
        }
    }

    override def toString = "AddTextOperation pos=%d t=%s p=%s".format(startPos, text, pword)

    override def equals(obj: Any) =
        obj match {
            case other: AddTextOperation =>
                text == other.text && startPos == other.startPos && pword == other.pword
            case _ => false
        }
}
