package ar.noxit.paralleleditor.kernel.basic.sync

import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.kernel.basic.{SynchronizerFactory, Synchronizer}
import reflect.BeanProperty
import ar.noxit.paralleleditor.common._

class SynchronizerAdapterFactory extends SynchronizerFactory {
    @BeanProperty
    var strategy: XFormStrategy = _

    def newSynchronizer: Synchronizer =
        new SynchronizerAdapter(new EditOperationJupiterSynchronizer(strategy))
}

class SynchronizerAdapter(val sync: JupiterSynchronizer[EditOperation]) extends Synchronizer {
    def receive(message: Message[EditOperation], apply: (EditOperation) => Unit) =
        sync.receive(message, apply)

    def generate(op: EditOperation, send: Message[EditOperation] => Unit) =
        sync.generate(op, send)
}
