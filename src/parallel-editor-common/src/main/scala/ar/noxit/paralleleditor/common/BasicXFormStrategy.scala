package ar.noxit.paralleleditor.common

import operation.{CompositeOperation, EditOperation, DeleteTextOperation, AddTextOperation}

class BasicXFormStrategy extends XFormStrategy {
    def xform(ops: (EditOperation, EditOperation)) = {
        if (ops == null)
            throw new IllegalArgumentException("ops cannot be null")

        ops match {
            case (c: AddTextOperation, s: AddTextOperation) => {
                if (c.startPos == s.startPos)
                    (new AddTextOperation(c.text, c.startPos), new AddTextOperation(s.text, c.startPos + c.text.length))
                else if (c.startPos < s.startPos)
                    (c, new AddTextOperation(s.text, s.startPos + c.text.length))
                else
                    (new AddTextOperation(c.text, c.startPos + s.text.length), s)
            }
            case (c: DeleteTextOperation, s: AddTextOperation) => {
                val res = xform(s, c)
                (res._2, res._1)
            }
            case (c: DeleteTextOperation, s: DeleteTextOperation) => {
                val intersection = getRangeFor(c) intersect getRangeFor(s)

                val min = if (c.startPos > s.startPos) s else c
                val max = if (c.startPos <= s.startPos) s else c

                if (intersection.isEmpty) {
                    val dt = new DeleteTextOperation(max.startPos - min.size, max.size)
                    if (min == c)
                        (min, dt)
                    else
                        (dt, min)
                } else {
                    val noSuperpuestosDeMin = getRangeFor(min) diff intersection
                    val noSuperpuestosDeMax = getRangeFor(max) diff intersection

                    (new DeleteTextOperation(min.startPos, noSuperpuestosDeMin.size), new DeleteTextOperation(min.startPos, noSuperpuestosDeMax.size))
                }
            }
            case (c: AddTextOperation, s: DeleteTextOperation) => {
                val deletionRange = getRangeFor(s)
                if (deletionRange contains c.startPos) {
                    (new AddTextOperation(c.text, s.startPos),
                            new CompositeOperation(
                                new DeleteTextOperation(s.startPos, (c.startPos - s.startPos)),
                                new DeleteTextOperation(s.startPos + c.text.length, s.startPos + s.size - c.startPos)))
                } else {
                    if (c.startPos < s.startPos)
                        (c, new DeleteTextOperation(s.startPos + c.text.length, s.size))
                    else
                        (new AddTextOperation(c.text, c.startPos - s.size), s)
                }
            }
        }
    }

    private def getRangeFor(o: DeleteTextOperation) = o.startPos to (o.startPos + o.size)
}
