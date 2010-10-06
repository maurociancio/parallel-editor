package ar.noxit.paralleleditor.common

import ar.noxit.paralleleditor.common.operation.{EditOperation, DeleteTextOperation, AddTextOperation}

class BasicXFormStrategy extends XFormStrategy {
    def xform(ops: (EditOperation, EditOperation)) = {
        if (ops == null)
            throw new IllegalArgumentException("ops cannot be null")

        ops match {
            case (c: AddTextOperation, s: AddTextOperation) => {
                if (c.startPos == s.startPos) {
                    (new AddTextOperation(c.text, c.startPos),
                            new AddTextOperation(s.text, c.startPos + c.text.length))
                } else if (c.startPos < s.startPos) {
                    (c,
                            new AddTextOperation(s.text, s.startPos + c.text.length))
                } else {
                    (new AddTextOperation(c.text,c.startPos + s.text.length),
                            s)
                }
            }
            case (o1: AddTextOperation, o2: DeleteTextOperation) => {
                (null, null)
            }
            case (o1: DeleteTextOperation, o2: DeleteTextOperation) => {
                (null, null)
            }
            case (o1: DeleteTextOperation, o2: AddTextOperation) => {
                (null, null)
            }
        }
    }
}
