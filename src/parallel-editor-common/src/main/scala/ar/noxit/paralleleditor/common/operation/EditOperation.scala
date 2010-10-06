package ar.noxit.paralleleditor.common.operation

import ar.noxit.paralleleditor.common.operations.DocumentData

trait EditOperation {

    def executeOn(documentData: DocumentData)
}
