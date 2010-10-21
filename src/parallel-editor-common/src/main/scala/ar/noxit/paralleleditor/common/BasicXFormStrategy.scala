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

        val alfa1 = pw(c)
        val alfa2 = pw(s)

        if (menor(alfa1, alfa2) || (igual(alfa1, alfa2) && c1 < c2))
            c
        else if (mayor(alfa1, alfa2) || (igual(alfa1, alfa2) && c1 > c2))
            new AddTextOperation(c1, p1 + c2.length, p1 :: w1)
        else
            c
    }

    /**
     * Caso borrar-borrar
     * para operaciones de borrado de 1 caracter
     */
    protected def xform(c: DeleteTextOperation, s: DeleteTextOperation): (EditOperation, EditOperation) = {
        if((s.size!=1)||(c.size!=1)) throw new UnsopportedEditOperationException("Delete size must be 1")

        val p1 = c.startPos
        val p2 = s.startPos

        if (p1 < p2)
            (c,new DeleteTextOperation(p2-1,s.size))
        else if (p1 > p2)
            (new DeleteTextOperation(p1-1,c.size),s)
        else
            (new NullOperation,new NullOperation)
    }

    /**
     * Caso agregar-borrar
     * la implementación contempla solo operaciones de borrado
     * de un caracter
     */
    protected def xform(c: AddTextOperation, s: DeleteTextOperation): (EditOperation, EditOperation) = {

        if(s.size!=1) throw new UnsopportedEditOperationException("Delete size must be 1")

        val p1 = c.startPos
        val p2 = s.startPos
        val pw = c.pword

        if (p1 > p2)
            (new AddTextOperation(c.text, p1 - 1, p1 :: pw), s)
        else if (p1 < p2)
            (c, new DeleteTextOperation(p2 + c.text.length, s.size))
        else
            (new AddTextOperation(c.text, p1, p1 :: pw), new DeleteTextOperation(p2 + c.text.length, s.size))
            
    }

    /**
     * público para testing
     */
    def pw(op: EditOperation) = {
        op match {
            case at: AddTextOperation => {
                // primer caso si w == vacio, con w = pword
                val p = at.startPos
                val w = at.pword

                if (w.isEmpty)
                    List(p)
                else if (!w.isEmpty && (p == current(w) || (p - current(w)).abs == 1))
                    p :: w
                else
                    List()
            }
            case dt: DeleteTextOperation => {
                val p = dt.startPos
                List(p)
            }
            case o: NullOperation => List()
        }
    }

    protected def current(pword: List[Int]) = pword.head

    private def getRangeFor(o: DeleteTextOperation) = o.startPos to (o.startPos + o.size)

    def menor(a: List[Int], b: List[Int]) = comparar(a, b, {(v1, v2) => v1 < v2})

    def mayor(a: List[Int], b: List[Int]) = comparar(a, b, {(v1, v2) => v1 > v2})

    def igual(a: List[Int], b: List[Int]) = comparar(a, b, {(v1, v2) => v1 == v2})

    private def comparar(a: List[Int], b: List[Int], comp: (Int, Int) => Boolean) = {
        val tuples = a zip b
        val result = tuples.dropWhile {t => t._1 == t._2}
        if (result isEmpty)
        // aca una es mas larga q la otra
            comp(a.size, b.size)
        else {
            val head = result.head
            comp(head._1, head._2)
        }
    }
}
