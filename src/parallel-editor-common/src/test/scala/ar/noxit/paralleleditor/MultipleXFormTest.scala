package ar.noxit.paralleleditor

import common.operation.{EditOperation, DocumentData, AddTextOperation}
import common.{Message, EditOperationJupiterSynchronizer, BasicXFormStrategy}
import org.junit._
import org.scalatest.junit.AssertionsForJUnit

@Test
class MultipleSyncTest extends AssertionsForJUnit {
    private var c1: EditOperationJupiterSynchronizer = _

    private var s1: EditOperationJupiterSynchronizer = _
    private var s2: EditOperationJupiterSynchronizer = _

    private var c2: EditOperationJupiterSynchronizer = _

    @Before
    def before {
        s1 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        s2 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        c1 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
        c2 = new EditOperationJupiterSynchronizer(new BasicXFormStrategy)
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

    def docFromText(text: String) = new DocumentData {var data = text}
}
