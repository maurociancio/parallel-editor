package ar.noxit.paralleleditor

import common.operation._
import common.{Message, EditOperationJupiterSynchronizer, BasicXFormStrategy}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit

@Test
class MultipleSyncTest extends AssertionsForJUnit {
    private var c1: EditOperationJupiterSynchronizer = _

    private var s1: EditOperationJupiterSynchronizer = _

    private var c2: EditOperationJupiterSynchronizer = _

    private var s2: EditOperationJupiterSynchronizer = _

    private var c3: EditOperationJupiterSynchronizer = _

    private var s3: EditOperationJupiterSynchronizer = _

    @Before
    def before {
        s1 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        s2 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        c1 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        c2 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        c3 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        s3 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
    }

    @Test
    def testCaseAB {
        val c1Doc = docFromText("")
        val c2Doc = docFromText("")
        val serverDoc = docFromText("")

        // c1 ! op
        var m1: Message[EditOperation] = null
        val c1o1 = new AddTextOperation("a", 0)
        c1o1.executeOn(c1Doc)
        c1.generateMsg(c1o1, {m => m1 = m})

        Assert.assertEquals(c1Doc.data, "a")

        // c2 ! op
        var m2: Message[EditOperation] = null
        val c2o1 = new AddTextOperation("b", 0)
        c2o1.executeOn(c2Doc)
        c2.generateMsg(c2o1, {m => m2 = m})

        Assert.assertEquals(c2Doc.data, "b")

        // server recibe de c1
        var m1Server: Message[EditOperation] = null
        s1.receiveMsg(m1, {
            op =>
                op.executeOn(serverDoc)
                s2.generateMsg(op, {m => m1Server = m})
        })

        Assert.assertEquals(serverDoc.data, "a")

        // server recibe de c2
        var m2Server: Message[EditOperation] = null
        s2.receiveMsg(m2, {
            op =>
                op.executeOn(serverDoc)
                s1.generateMsg(op, {m => m2Server = m})
        })

        Assert.assertEquals(serverDoc.data, "ab")

        // server propaga al c1
        c1.receiveMsg(m2Server, {op => op.executeOn(c1Doc)})
        Assert.assertEquals(c1Doc.data, "ab")

        // server propaga al c2
        c2.receiveMsg(m1Server, {op => op.executeOn(c2Doc)})
        Assert.assertEquals(c2Doc.data, "ab")
    }

    /**
     * Ver paper
     * ftp://ftp.inria.fr/INRIA/publication/publi-pdf/RR/RR-5188.pdf
     * Hoja nro 8 pto 3.1
     */
    @Test
    def testCaseCoreToCoffe {
        val c1Doc = docFromText("core")
        val c2Doc = docFromText("core")
        val c3Doc = docFromText("core")
        val serverDoc = docFromText("core")

        // client 1, agregar f despues de la r
        val op1 = new AddTextOperation("f", 3)
        op1.executeOn(c1Doc)
        Assert.assertEquals("corfe", c1Doc.data)

        // client 2, borrar r
        val op2 = new DeleteTextOperation(startPos = 2, size = 1)
        op2.executeOn(c2Doc)
        Assert.assertEquals("coe", c2Doc.data)

        // client 3, agregar f dsps de o
        val op3 = new AddTextOperation("f", 2)
        op3.executeOn(c3Doc)
        Assert.assertEquals("cofre", c3Doc.data)

        // hasta aqui, todas las operaciones aplicadas a sus respectivos documentos locales
        // cada cliente empieza a broadcastear la operacion
        var op1Transmitida: Message[EditOperation] = null
        c1.generateMsg(op1, {m => op1Transmitida = m})
        Assert.assertEquals(0, op1Transmitida.myMsgs)
        Assert.assertEquals(0, op1Transmitida.otherMsgs)
        Assert.assertEquals(1, c1.myMsgs)
        Assert.assertEquals(0, c1.otherMsgs)

        var op2Transmitida: Message[EditOperation] = null
        c2.generateMsg(op2, {m => op2Transmitida = m})
        Assert.assertEquals(0, op2Transmitida.myMsgs)
        Assert.assertEquals(0, op2Transmitida.otherMsgs)
        Assert.assertEquals(1, c2.myMsgs)
        Assert.assertEquals(0, c2.otherMsgs)

        var op3Transmitida: Message[EditOperation] = null
        c3.generateMsg(op3, {m => op3Transmitida = m})
        Assert.assertEquals(0, op3Transmitida.myMsgs)
        Assert.assertEquals(0, op3Transmitida.otherMsgs)
        Assert.assertEquals(1, c3.myMsgs)
        Assert.assertEquals(0, c3.otherMsgs)

        // las operaciones transmitidas estan viajando hacia el server

        // primero se procesa la operacion 2 en el servidor
        // el servidor la re transmite a los otros syncs

        // retransmision hacia el cliente 1 de la operacion 2
        var op2toClient1: Message[EditOperation] = null
        // retransmision hacia el cliente 3 de la operacion 2
        var op2toClient3: Message[EditOperation] = null
        s2.receiveMsg(op2Transmitida, {
            op => op.executeOn(serverDoc)
            s1.generateMsg(op, {m => op2toClient1 = m})
            s3.generateMsg(op, {m => op2toClient3 = m})
        })
        Assert.assertEquals("coe", serverDoc.data)

        Assert.assertEquals(0, op2toClient1.myMsgs)
        Assert.assertEquals(0, op2toClient1.otherMsgs)

        Assert.assertEquals(0, op2toClient3.myMsgs)
        Assert.assertEquals(0, op2toClient3.otherMsgs)

        Assert.assertEquals(0, s2.myMsgs)
        Assert.assertEquals(1, s2.otherMsgs)
        Assert.assertEquals(1, s1.myMsgs)
        Assert.assertEquals(0, s1.otherMsgs)
        Assert.assertEquals(1, s3.myMsgs)
        Assert.assertEquals(0, s3.otherMsgs)

        // cliente 3 recibe la operacion 2 retransmitida por su sync del lado del server
        c3.receiveMsg(op2toClient3, {op => op.executeOn(c3Doc)})
        Assert.assertEquals("cofe", c3Doc.data)
        Assert.assertEquals(1, c3.myMsgs)
        Assert.assertEquals(1, c3.otherMsgs)

        //
        // el cliente 3 reciba la operacion 1 que fue retransmitida por su sync del lado del server
        //

        // retransmision del lado del server

        // retransmision hacia el cliente 2 de la operacion 1
        var op1toClient2: Message[EditOperation] = null
        // retransmision hacia el cliente 3 de la operacion 1
        var op1toClient3: Message[EditOperation] = null
        s1.receiveMsg(op1Transmitida, {
            op => op.executeOn(serverDoc)
            s2.generateMsg(op, {m => op1toClient2 = m})
            s3.generateMsg(op, {m => op1toClient3 = m})
        })
        Assert.assertEquals("cofe", serverDoc.data)

        Assert.assertEquals(0, op1toClient2.myMsgs)
        Assert.assertEquals(1, op1toClient2.otherMsgs)

        Assert.assertEquals(1, op1toClient3.myMsgs)
        Assert.assertEquals(0, op1toClient3.otherMsgs)

        Assert.assertEquals(1, s1.myMsgs)
        Assert.assertEquals(1, s1.otherMsgs)
        Assert.assertEquals(1, s2.myMsgs)
        Assert.assertEquals(1, s2.otherMsgs)
        Assert.assertEquals(2, s3.myMsgs)
        Assert.assertEquals(0, s3.otherMsgs)

        // el cliente 3 reciba la operacion 1 retransmitida por su sync del lado del server
        c3.receiveMsg(op1toClient3, {op => op.executeOn(c3Doc)})
        Assert.assertEquals("coffe", c3Doc.data)
    }

    def pw(op: EditOperation) = {
        op match {
            case at: AddTextOperation => {
                // primer caso si w == vacio, con w = pword
                if (at.pword.isEmpty)
                    Some(at.startPos.toString)
                else {
                    if (at.pword.isDefined && (math.abs(at.startPos - current(at.pword.get)) <= 1)) {
                        Some(at.startPos.toString + at.pword.get)
                    } else {
                        None
                    }
                }
            }
            case dt: DeleteTextOperation => {
                Some(dt.startPos)
            }
            case o: NullOperation =>
                None
        }
    }

    def current(text: String) = {
        val first = text.substring(0, 1)
        first.toInt
    }

    @Test
    def testPword: Unit = {
        Assert.assertEquals(Some("0"), pw(new AddTextOperation("hola", 0)))
        Assert.assertEquals(Some("01"), pw(new AddTextOperation("hola", 0, Some("1"))))
        Assert.assertEquals(None, pw(new AddTextOperation("hola", 0, Some("5"))))
        Assert.assertEquals(Some(0), pw(new DeleteTextOperation(startPos = 0, size = 10)))
    }

    def docFromText(text: String) = new DocumentData {var data = text}
}
