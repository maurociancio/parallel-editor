package ar.noxit.paralleleditor.common.operation

trait Caret {
    def offset: Int
    def selectionLength: Int
    def change(offset: Int, selectionLength: Int)
}

trait DocumentData {
    var data: String
    val caret: Caret
}
