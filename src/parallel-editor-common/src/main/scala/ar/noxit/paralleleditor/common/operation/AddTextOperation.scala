package ar.noxit.paralleleditor.common.operation

class AddTextOperation(val text: String, val startPos: Int, val pword: Option[String] = None) extends EditOperation {

    def executeOn(documentData: DocumentData) = {
        val original = documentData.data
        documentData.data = original.substring(0, startPos) + text + original.substring(startPos)
    }

    override def toString = {
        "AddTextOperation pos=%d t=%s".format(startPos, text)
    }
}
