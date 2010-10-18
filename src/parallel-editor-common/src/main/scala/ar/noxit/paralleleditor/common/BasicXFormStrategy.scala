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
            case (c: EditOperation, s: EditOperation) if c.isInstanceOf[NullOperation] || s.isInstanceOf[NullOperation] =>
                (c, s)
        }
    }

    /**
     * Caso agregar-agregar
     */
    protected def xform(c: AddTextOperation, s: AddTextOperation): (EditOperation, EditOperation) = {
        (simpleXForm(c, s), simpleXForm(s, c))
    }

    /**
     * Implementación según paper
     * Achieving Convergence with Operational
     * Transformation in Distributed Groupware Systems
     */
    protected def simpleXForm(c: AddTextOperation, s: AddTextOperation) = {
        val p1 = c.startPos
        val p2 = s.startPos
        val c1 = c.text
        val c2 = s.text
        val w1 = c.pword

        val alfa1 = pw(c).getOrElse(p1)
        val alfa2 = pw(s).getOrElse(p2)

        if (alfa1 < alfa2 || (alfa1 == alfa2 && c1 < c2)) {
            c
        } else if (alfa1 > alfa2 || (alfa1 == alfa2 && c1 > c2)) {
            new AddTextOperation(c1, p1 + c2.length, p1.toString + w1)
        } else {
            c
        }
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
            (new AddTextOperation(c.text, s.startPos, c.startPos.toString + c.pword),
                    new CompositeOperation(
                        new DeleteTextOperation(s.startPos, (c.startPos - s.startPos)),
                        new DeleteTextOperation(s.startPos + c.text.length, s.startPos + s.size - c.startPos)))
        } else {
            if (c.startPos < s.startPos)
                (c, new DeleteTextOperation(s.startPos + c.text.length, s.size))
            else
                (new AddTextOperation(c.text, c.startPos - s.size, c.startPos.toString + c.pword), s)
        }
    }

    protected def pw(op: EditOperation): Option[Int] = {
        op match {
            case at: AddTextOperation => {
                // primer caso si w == vacio, con w = pword
                val p = at.startPos
                val w = at.pword

                if (w.isEmpty)
                    Some(p)
                else if (!w.isEmpty && (p - current(w)).abs <= 1) {
                    Some((p.toString + w).toInt)
                } else {
                    None
                }
            }
            case dt: DeleteTextOperation => {
                val p = dt.startPos
                Some(p)
            }
            case o: NullOperation =>
                None
        }
    }

    protected def current(text: String) = {
        val first = text.substring(0, 1)
        first.toInt
    }

    private def getRangeFor(o: DeleteTextOperation) = o.startPos to (o.startPos + o.size)
}
