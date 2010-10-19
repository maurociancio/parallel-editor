package ar.noxit.paralleleditor

import common.operation._
import common.{Message, EditOperationJupiterSynchronizer, BasicXFormStrategy}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import collection.mutable.Queue

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
        c1.generate(c1o1, {m => m1 = m})

        Assert.assertEquals(c1Doc.data, "a")

        // c2 ! op
        var m2: Message[EditOperation] = null
        val c2o1 = new AddTextOperation("b", 0)
        c2o1.executeOn(c2Doc)
        c2.generate(c2o1, {m => m2 = m})

        Assert.assertEquals(c2Doc.data, "b")

        // server recibe de c1
        var m1Server: Message[EditOperation] = null
        s1.receive(m1, {
            op =>
                op.executeOn(serverDoc)
                s2.generate(op, {m => m1Server = m})
        })

        Assert.assertEquals(serverDoc.data, "a")

        // server recibe de c2
        var m2Server: Message[EditOperation] = null
        s2.receive(m2, {
            op =>
                op.executeOn(serverDoc)
                s1.generate(op, {m => m2Server = m})
        })

        Assert.assertEquals(serverDoc.data, "ab")

        // server propaga al c1
        c1.receive(m2Server, {op => op.executeOn(c1Doc)})
        Assert.assertEquals(c1Doc.data, "ab")

        // server propaga al c2
        c2.receive(m1Server, {op => op.executeOn(c2Doc)})
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
        c1.generate(op1, {m => op1Transmitida = m})
        Assert.assertEquals(0, op1Transmitida.myMsgs)
        Assert.assertEquals(0, op1Transmitida.otherMsgs)
        Assert.assertEquals(1, c1.myMsgs)
        Assert.assertEquals(0, c1.otherMsgs)

        var op2Transmitida: Message[EditOperation] = null
        c2.generate(op2, {m => op2Transmitida = m})
        Assert.assertEquals(0, op2Transmitida.myMsgs)
        Assert.assertEquals(0, op2Transmitida.otherMsgs)
        Assert.assertEquals(1, c2.myMsgs)
        Assert.assertEquals(0, c2.otherMsgs)

        var op3Transmitida: Message[EditOperation] = null
        c3.generate(op3, {m => op3Transmitida = m})
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
        s2.receive(op2Transmitida, {
            op => op.executeOn(serverDoc)
            s1.generate(op, {m => op2toClient1 = m})
            s3.generate(op, {m => op2toClient3 = m})
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
        c3.receive(op2toClient3, {op => op.executeOn(c3Doc)})
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
        s1.receive(op1Transmitida, {
            op => op.executeOn(serverDoc)
            s2.generate(op, {m => op1toClient2 = m})
            s3.generate(op, {m => op1toClient3 = m})
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
        c3.receive(op1toClient3, {op => op.executeOn(c3Doc)})
        Assert.assertEquals("coffe", c3Doc.data)


        // el sync del cliente 3 del lado del server recibe la operación 3
        var op3toClient2: Message[EditOperation] = null
        s3.receive(op3Transmitida, {
            op => op.executeOn(serverDoc)
            s2.generate(op, {m => op3toClient2 = m})
        })
        Assert.assertEquals("coffe", serverDoc.data)

        // el cliente 2 recibe la operacion 3 transformada desde el server
        c2.receive(op3toClient2, {op => op.executeOn(c2Doc)})
        Assert.assertEquals("cofe", c2Doc.data)

        // el cliente 2 recibe la operacion 1 transformada desde el server
        c2.receive(op1toClient2, {op => op.executeOn(c2Doc)})
        Assert.assertEquals("coffe", c2Doc.data)
    }

    @Test
    def testPword: Unit = {
        val xf = new BasicXFormStrategy
        Assert.assertEquals(Some(0), xf.pw(new AddTextOperation("hola", 0)))
        Assert.assertEquals(Some(1), xf.pw(new AddTextOperation("hola", 0, "1")))
        Assert.assertEquals(None, xf.pw(new AddTextOperation("hola", 0, "5")))
        Assert.assertEquals(Some(0), xf.pw(new DeleteTextOperation(startPos = 0, size = 10)))
    }

    @Test
    def testEscribirTextoLineasDistintas: Unit = {
        val c1Doc = docFromText("\n\n")
        val c2Doc = docFromText("\n\n")
        val serverDoc = docFromText("\n\n")

        val textClient1 = "escribiendo en letras minusculas"
        val textClient2 = "E STO ES UNA PRUEBA EN MAYUSCULA"

        // usamos frases de misma longitud
        Assert.assertEquals(textClient1.size, textClient2.size)

        // el cliente 1 escribe "escri" en la ultima linea del documento
        // los mensajes se guardan en la cola
        val colaC1 = Queue[Message[EditOperation]]()
        val startPosC1 = c1Doc.data.size
        for (i <- 0 until 7) {
            val c = textClient1.substring(i, i + 1)

            val op = new AddTextOperation(c, startPosC1 + i)
            op.executeOn(c1Doc)

            c1.generate(op, {msg => colaC1 += msg})
        }

        Assert.assertEquals("\n\nescribi", c1Doc.data)

        // el cliente 2 escribe "E STO E" al inicio del doc
        val colaC2 = Queue[Message[EditOperation]]()
        val startPosC2 = 0
        for (i <- 0 until 7) {
            val c = textClient2.substring(i, i + 1)

            val op = new AddTextOperation(c, startPosC2 + i)
            op.executeOn(c2Doc)

            c2.generate(op, {msg => colaC2 += msg})
        }

        Assert.assertEquals("E STO E\n\n", c2Doc.data)


        // los sync del server comienzan a recibir los msgs
        (0 until List(colaC1.size, colaC2.size).min).foreach {

            i =>
            println(i)
            println(serverDoc.data)

            s1.receive(colaC1.dequeue, {
                op => op.executeOn(serverDoc)
                s2.generate(op, {msg =>})
            })

            s2.receive(colaC2.dequeue, {
                op => op.executeOn(serverDoc)
                s1.generate(op, {msg =>})
            })
        }

        Assert.assertEquals("E STO E\n\nescribi", serverDoc.data)
    }

    def docFromText(text: String) = new DocumentData {var data = text}
}
