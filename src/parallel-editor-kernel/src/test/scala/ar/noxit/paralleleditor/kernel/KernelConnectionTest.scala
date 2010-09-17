package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.actors.ClientActor
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.KernelActor
import ar.noxit.paralleleditor.kernel.remote.KernelService
import org.junit._
import Assert._
import org.scalatest.junit.AssertionsForJUnit
import scala.actors.Actor._

@Test
class KernelConnectionTest extends AssertionsForJUnit {

    @Test
    def testDocumentCount : Unit = {
        val kernel = new BasicKernel
        val ka = new KernelActor(kernel).start

        val nullActor = actor{
            loop {
                receive {
                    case any =>
                        println("null actor " + any)
                }
            }
        }
        val client = new ClientActor(ka, nullActor).start

        client ! "myUsername"
        client ! "doclist"
        client ! ("newdoc", "my title")
        Thread.sleep(4000)
    }
}
