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

        val alfa1:String = pw(c).getOrElse(p1.toString)
        val alfa2:String = pw(s).getOrElse(p2.toString)
        

        if (mayor(alfa2,alfa1) || (alfa1 == alfa2 && c1 < c2)) {
            c
        } else if (mayor(alfa1,alfa2) || (alfa1==alfa2 && c1 > c2)) {
            new AddTextOperation(c1, p1 + c2.length, trimPrefix(p1.toString,"0") + w1)
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
    protected def xform(c: AddTextOperation, s: DeleteTextOperation): (EditOperation, EditOperation) = {
        val deletionRange = getRangeFor(s)
        val exclusiveDeletionRange = (deletionRange slice (1,deletionRange.size -1))
        val insertionRange = c.startPos to (c.startPos + c.text.length)

        if ( exclusiveDeletionRange contains c.startPos) {
            // el rango de borrado incluye a la posicion de insercion
            val endPos = s.startPos + s.size + insertionRange.size -1

           (new NullOperation(),new DeleteTextOperation(s.startPos,endPos-s.startPos))

        } else {
            //  el punto de insercion no esta dentro del rango de borrado
            if (c.startPos <= s.startPos)
                (c, new DeleteTextOperation(s.startPos + c.text.length, s.size))
            else
                (new AddTextOperation(c.text, c.startPos - s.size,trimPrefix( c.startPos.toString,"0") + c.pword), s)
        }
    }

    /**
     * público para testing
     */
    def pw(op: EditOperation): Option[String] = {
        op match {
            case at: AddTextOperation => {
                // primer caso si w == vacio, con w = pword
                val p = at.startPos
                val w = at.pword

                if (w.isEmpty)
                    Some(p.toString)
                else if (!w.isEmpty && (p - current(w)).abs <= 1) {
                    Some(trimPrefix(p.toString,"0") + w)
                } else {
                    None
                }
            }
            case dt: DeleteTextOperation => {
                val p = dt.startPos
                Some(p.toString)
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

    private def trimPrefix(s:String,p:String):String = if (s startsWith p) trimPrefix(s substring p.length, p) else s

    private def mayor(a:String,b:String)={
        val s1 = trimPrefix(a,"0")
        val s2 = trimPrefix(b,"0")
        if(s1.length == s2.length)
            s1 > s2
        else if (s1.length > s2.length) true
        else false
    }
}
