package ar.noxit.paralleleditor.kernel.basic.sync

import ar.noxit.paralleleditor.kernel.basic.Synchronizer
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.{JupiterSynchronizer, Message}

class SynchronizerAdapter(val sync: JupiterSynchronizer[EditOperation]) extends Synchronizer {
    def receive(message: Message[EditOperation], apply: (EditOperation) => Unit) =
        sync.receive(message, apply)

    def generate(op: EditOperation, send: Message[EditOperation] => Unit) =
        sync.generate(op, send)
}
