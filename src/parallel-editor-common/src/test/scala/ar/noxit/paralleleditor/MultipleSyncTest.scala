/*
 *  A real-time collaborative tool to develop files over the network.
 *  Copyright (C) 2010  Mauro Ciancio and Leandro Gilioli
 *                      {maurociancio,legilioli} at gmail dot com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
        Assert.assertEquals(List(0), xf.pw(new AddTextOperation("hola", 0)))
        Assert.assertEquals(List(0, 1), xf.pw(new AddTextOperation("hola", 0, List(1))))
        Assert.assertEquals(List(), xf.pw(new AddTextOperation("hola", 0, List(5))))
        Assert.assertEquals(List(0), xf.pw(new DeleteTextOperation(startPos = 0, size = 10)))
    }

    @Test
    def testMenorPword: Unit = {
        val xf = new BasicXFormStrategy
        val p1 = List(1, 2, 3)
        val p2 = List(1, 2, 4)

        Assert.assertEquals(true, xf.igual(p1, p1))

        Assert.assertEquals(false, xf.menor(p1, p1))
        Assert.assertEquals(false, xf.mayor(p1, p1))

        Assert.assertEquals(true, xf.menor(p1, p2))
        Assert.assertEquals(false, xf.mayor(p1, p2))
    }

    @Test
    def testMenorPword2: Unit = {
        val xf = new BasicXFormStrategy
        val p1 = List(1, 2, 3, 4)
        val p2 = List(1, 2, 3)

        Assert.assertEquals(false, xf.menor(p1, p2))
        Assert.assertEquals(true, xf.mayor(p1, p2))

        Assert.assertEquals(false, xf.igual(p1, p2))
    }

    @Test
    def testMenorPword3: Unit = {
        val xf = new BasicXFormStrategy
        val p1 = List(1, 2, 3, 4)
        val p2 = List(5, 2, 3)

        Assert.assertEquals(true, xf.menor(p1, p2))
        Assert.assertEquals(false, xf.mayor(p1, p2))

        Assert.assertEquals(false, xf.menor(List(), List()))
        Assert.assertEquals(false, xf.mayor(List(), List()))

        Assert.assertEquals(false, xf.menor(List(2), List()))
        Assert.assertEquals(true, xf.mayor(List(2), List()))

        Assert.assertEquals(true, xf.menor(List(), List(2)))
        Assert.assertEquals(false, xf.mayor(List(), List(2)))
    }

    @Test
    def testEscribirTextoLineasDistintas: Unit = {
        val c1Doc = docFromText("--")
        val c2Doc = docFromText("--")
        val serverDoc = docFromText("--")

        val textClient1 = "escribi"
        val textClient2 = "ABCDEFG"

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

            c1.generate(op, {
                msg => colaC1 += msg
            })
        }

        Assert.assertEquals("--escribi", c1Doc.data)

        // el cliente 2 escribe "ABCDEFG" al inicio del doc
        val colaC2 = Queue[Message[EditOperation]]()
        val startPosC2 = 0
        for (i <- 0 until 7) {
            val c = textClient2.substring(i, i + 1)

            val op = new AddTextOperation(c, startPosC2 + i)
            op.executeOn(c2Doc)

            c2.generate(op, {
                msg => colaC2 += msg
            })
        }

        Assert.assertEquals("ABCDEFG--", c2Doc.data)


        val colaACliente1 = Queue[Message[EditOperation]]()
        val colaACliente2 = Queue[Message[EditOperation]]()

        // los sync del server comienzan a recibir los msgs
        (0 until 7).foreach {

            i =>
                println(i)
                println(serverDoc.data)

                s1.receive(colaC1.dequeue, {
                    op => op.executeOn(serverDoc)
                    s2.generate(op, {
                        msg => colaACliente2 += msg
                    })
                })

                s2.receive(colaC2.dequeue, {
                    op => op.executeOn(serverDoc)
                    s1.generate(op, {
                        msg => colaACliente1 += msg
                    })
                })
        }

        Assert.assertEquals("ABCDEFG--escribi", serverDoc.data)

        colaACliente1.foreach {
            m => c1.receive(m, {
                op => op.executeOn(c1Doc)
            })
        }
        colaACliente2.foreach {
            m => c2.receive(m, {
                op => op.executeOn(c2Doc)
            })
        }

        Assert.assertEquals("ABCDEFG--escribi", c1Doc.data)
        Assert.assertEquals("ABCDEFG--escribi", c2Doc.data)
    }

    @Test
    def testEscribirTextoMismaLinea: Unit = {
        val c1Doc = docFromText("")
        val c2Doc = docFromText("")
        val serverDoc = docFromText("")

        val textClient1 = "HOLA"
        val textClient2 = "chau"

        // el cliente 1 escribe "HOLA" en la primer posicion del documento
        // los mensajes se guardan en la cola
        val colaC1 = Queue[Message[EditOperation]]()
        val startPosC1 = 0
        for (i <- 0 until 4) {
            val c = textClient1.substring(i, i + 1)

            val op = new AddTextOperation(c, startPosC1 + i)
            op.executeOn(c1Doc)

            c1.generate(op, {
                msg => colaC1 += msg
            })
        }

        Assert.assertEquals("HOLA", c1Doc.data)

        // el cliente 2 escribe "chau" al inicio del doc
        val colaC2 = Queue[Message[EditOperation]]()
        val startPosC2 = 0
        for (i <- 0 until 4) {
            val c = textClient2.substring(i, i + 1)

            val op = new AddTextOperation(c, startPosC2 + i)
            op.executeOn(c2Doc)

            c2.generate(op, {
                msg => colaC2 += msg
            })
        }

        Assert.assertEquals("chau", c2Doc.data)

        val colaACliente1 = Queue[Message[EditOperation]]()
        val colaACliente2 = Queue[Message[EditOperation]]()

        // los sync del server comienzan a recibir los msgs
        (0 until 4).foreach {
            i =>
                println(i)
                println(serverDoc.data)

                s1.receive(colaC1.dequeue, {
                    op =>
                        op.executeOn(serverDoc)

                        s2.generate(op, {
                            msg => colaACliente2 += msg
                        })
                })

                s2.receive(colaC2.dequeue, {
                    op =>
                        op.executeOn(serverDoc)

                        s1.generate(op, {
                            msg => colaACliente1 += msg
                        })
                })
        }

        Assert.assertEquals("HOLAchau", serverDoc.data)

        (0 until 4).foreach {
            i =>
                c1.receive(colaACliente1.dequeue, {
                    op => op.executeOn(c1Doc)
                })

                c2.receive(colaACliente2.dequeue, {
                    op => op.executeOn(c2Doc)
                })
        }

        Assert.assertEquals("HOLAchau", c1Doc.data)
        Assert.assertEquals("HOLAchau", c2Doc.data)
    }

    @Test
    def testEscribirTextoYBorrar: Unit = {
        val c1Doc = docFromText("ABCD1234")
        val c2Doc = docFromText("ABCD1234")
        val serverDoc = docFromText("ABCD1234")

        val textClient1 = "WXYZ"
        val startPosC1 = 6
        val colaC1 = Queue[Message[EditOperation]]()

        (0 until textClient1.length).foreach {
            i =>
                val c = textClient1.substring(i, i + 1)

                val op = new AddTextOperation(c, startPosC1 + i)
                op.executeOn(c1Doc)

                c1.generate(op, {
                    msg => colaC1 += msg
                })
        }

        val startPosC2 = 4
        val delSizeC2 = 4
        val colaC2 = Queue[Message[EditOperation]]()

        (1 to delSizeC2).foreach {
            i =>
                val op = new DeleteTextOperation(startPosC2, 1)
                op.executeOn(c2Doc)
                c2.generate(op, {
                    msg => colaC2 += msg
                })
        }

        //el servidor recibe los mensajes intercalados

        val colaACliente1 = Queue[Message[EditOperation]]()
        val colaACliente2 = Queue[Message[EditOperation]]()

        (0 until 4).foreach {
            i =>
                println(i)
                println(serverDoc.data)

                s1.receive(colaC1.dequeue, {
                    op =>
                        op.executeOn(serverDoc)

                        s2.generate(op, {
                            msg => colaACliente2 += msg
                        })
                })

                s2.receive(colaC2.dequeue, {
                    op =>
                        op.executeOn(serverDoc)

                        s1.generate(op, {
                            msg => colaACliente1 += msg
                        })
                })
        }

        Assert.assertEquals("ABCDWXYZ", serverDoc.data)

        (0 until 4).foreach {
            i =>
                c1.receive(colaACliente1.dequeue, {
                    op => op.executeOn(c1Doc)
                })

                c2.receive(colaACliente2.dequeue, {
                    op => op.executeOn(c2Doc)
                })
        }

        Assert.assertEquals("ABCDWXYZ", c1Doc.data)
        Assert.assertEquals("ABCDWXYZ", c2Doc.data)
    }

    def docFromText(text: String) = new MockDocument(text)
}
