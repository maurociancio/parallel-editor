package ar.noxit.paralleleditor.kernel.basic.sync

import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.{EditOperationJupiterSynchronizer, BasicXFormStrategy, JupiterSynchronizer, Message}
import ar.noxit.paralleleditor.kernel.basic.{SynchronizerFactory, Synchronizer}

class SynchronizerAdapterFactory extends SynchronizerFactory {
    def newSynchronizer: Synchronizer =
        new SynchronizerAdapter(new EditOperationJupiterSynchronizer(new BasicXFormStrategy))
}

class SynchronizerAdapter(val sync: JupiterSynchronizer[EditOperation]) extends Synchronizer {
    def receive(message: Message[EditOperation], apply: (EditOperation) => Unit) =
        sync.receive(message, apply)

    def generate(op: EditOperation, send: Message[EditOperation] => Unit) =
        sync.generate(op, send)
}
