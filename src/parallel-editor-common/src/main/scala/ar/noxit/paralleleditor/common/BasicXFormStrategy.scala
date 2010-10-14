package ar.noxit.paralleleditor.common

import operation._

class BasicXFormStrategy extends XFormStrategy {
    override def xform(ops: (EditOperation, EditOperation)) = {
        if (ops == null)
            throw new IllegalArgumentException("ops cannot be null")

        ops match {
            case (c: AddTextOperation, s: AddTextOperation) =>
                xform(c, s)
            case (c: DeleteTextOperation, s: DeleteTextOperation) =>
                xform(c, s)
            case (c: AddTextOperation, s: DeleteTextOperation) =>
                xform(c, s)
            case (c: DeleteTextOperation, s: AddTextOperation) =>
                xform(s, c).swap
        }
    }

    /**
     * Caso agregar-agregar
     */
    protected def xform(c: AddTextOperation, s: AddTextOperation): (EditOperation, EditOperation) = {
        val cpos = c.startPos
        val spos = s.startPos
        val ctext = c.text
        val stext = s.text

        if (cpos < spos || (cpos == spos && ctext < stext)) {
            (c, new AddTextOperation(stext, spos + ctext.length))
        } else if (cpos > spos || (cpos == spos && ctext > stext)) {
            (new AddTextOperation(ctext, cpos + stext.length), s)
        } else {
            (NullOperation, NullOperation)
        }
        //        if (c.startPos == s.startPos) {
        //            (new AddTextOperation(c.text, c.startPos), new AddTextOperation(s.text, c.startPos + c.text.length))
        //            //            val clen = c.text.length
        //            //            val slen = s.text.length
        //            //            if (clen == slen) {
        //            //                val res = c.text.compare(s.text)
        //            //                if (res == 0) {
        //            //                    (c, s)
        //            //                } else {
        //            //                    val min = if (res < 0) s else c
        //            //                    val max = if (res > 0) s else c
        //            //                    if (min == c)
        //            //                        (new AddTextOperation(min.text, min.startPos), new AddTextOperation(max.text, min.startPos + min.text.length))
        //            //                    else
        //            //                        (new AddTextOperation(max.text, min.startPos + min.text.length), new AddTextOperation(min.text, min.startPos))
        //            //                }
        //            //            } else {
        //            //                val min = if (clen > slen) s else c
        //            //                val max = if (clen > slen) c else s
        //            //                (new AddTextOperation(min.text, min.startPos), new AddTextOperation(max.text, min.startPos + min.text.length))
        //            //            }
        //        }
        //        else if (c.startPos < s.startPos)
        //            (c, new AddTextOperation(s.text, s.startPos + c.text.length))
        //        else
        //            (new AddTextOperation(c.text, c.startPos + s.text.length), s)
    }

    /**
     * Caso borrar-borrara
     */
    protected def xform(c: DeleteTextOperation, s: DeleteTextOperation): (DeleteTextOperation, DeleteTextOperation) = {
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

    /**
     * Caso agregar-borrar
     */
    protected def xform(c: AddTextOperation, s: DeleteTextOperation): (AddTextOperation, EditOperation) = {
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

    private def getRangeFor(o: DeleteTextOperation) = o.startPos to (o.startPos + o.size)
}
