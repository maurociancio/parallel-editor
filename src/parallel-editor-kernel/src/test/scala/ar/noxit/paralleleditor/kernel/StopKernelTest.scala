package ar.noxit.paralleleditor.kernel

import actors.KernelActor
import basic.sync.SynchronizerAdapterFactory
import ar.noxit.paralleleditor.common.BasicXFormStrategy
import basic.BasicKernel
import messages.TerminateKernel
import org.junit._
import org.scalatest.junit.AssertionsForJUnit
import org.easymock.EasyMock._

@Test
class StopKernelTest extends AssertionsForJUnit {
    var kernel: BasicKernel = _
    var docSession: DocumentSession = _

    @Before
    def setUp = {
        kernel = new BasicKernel;
        val synchronizerAdapterFactory = new SynchronizerAdapterFactory
        synchronizerAdapterFactory.strategy = new BasicXFormStrategy
        kernel.sync = synchronizerAdapterFactory
        kernel.timeout = 5000
        docSession = null
    }

    @Test
    def testKernel: Unit = {
        val session = kernel.login("username")

        val callback = createMock(classOf[UpdateCallback])
        callback.update(anyObject())
        replay(callback)

        session.installOnUpdateCallback(callback)

        kernel.newDocument(session, "title", "content")
        val doc = kernel.documentByTitle("title")
        kernel.terminate


        Thread.sleep(1000)
        verify(callback)
    }

    @Test
    def testKernelActor: Unit = {
        val ka = new KernelActor(kernel)
        ka.start
        ka ! TerminateKernel()

        Thread.sleep(1000)
    }
}
