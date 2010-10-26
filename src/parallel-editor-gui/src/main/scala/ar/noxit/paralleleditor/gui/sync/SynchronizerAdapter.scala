package ar.noxit.paralleleditor.gui.sync

import ar.noxit.paralleleditor.gui.Synchronizer
import ar.noxit.paralleleditor.common.operation.EditOperation
import ar.noxit.paralleleditor.common.{EditOperationJupiterSynchronizer, Message}

class SynchronizerAdapter(private val sync: EditOperationJupiterSynchronizer) extends Synchronizer {
    override def generate(op: EditOperation, send: (Message[EditOperation]) => Unit) =
        sync.generate(op, send)

    override def receive(message: Message[EditOperation], apply: (EditOperation) => Unit) =
        sync.receive(message, apply)
}
