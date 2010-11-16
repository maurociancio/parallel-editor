package ar.noxit.paralleleditor.common.operation

trait Caret {
    val offset: Int
    val selectionLength: Int
    def change(offset: Int, selectionLength: Int)
}

trait DocumentData {
    var data: String
    val caret: Caret
}
