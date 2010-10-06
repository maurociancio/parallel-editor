package ar.noxit.paralleleditor.common

import operation.EditOperation

trait XFormStrategy {
    def xform(ops: (EditOperation, EditOperation)): (EditOperation, EditOperation)
}
