package ar.noxit.paralleleditor.common.operation

trait Caret {
    def offset: Int
    def selectionLength: Int
    def change(offset: Int, selectionLength: Int)
}

trait DocumentData {
    def data: String
    def replace(offset: Int, length: Int, newText: String)
    val caret: Caret
}
