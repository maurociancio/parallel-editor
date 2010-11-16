package ar.noxit.paralleleditor

import common.operation.Caret

class NullCaret extends Caret {
    val selectionLength = 0
    val offset = 0

    def change(offset: Int, selectionLength: Int) = {}
}
