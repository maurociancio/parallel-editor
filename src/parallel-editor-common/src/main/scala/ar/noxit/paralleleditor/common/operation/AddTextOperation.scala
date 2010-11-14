package ar.noxit.paralleleditor.common.operation

class AddTextOperation(val text: String, val startPos: Int, val pword: List[Int] = List()) extends EditOperation {
    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + text + original.substring(startPos)
    }

    override def toString = "AddTextOperation pos=%d t=%s p=%s".format(startPos, text, pword)

    override def equals(obj: Any) =
        obj match {
            case other: AddTextOperation =>
                text == other.text && startPos == other.startPos && pword == other.pword
            case _ => false
        }
}
