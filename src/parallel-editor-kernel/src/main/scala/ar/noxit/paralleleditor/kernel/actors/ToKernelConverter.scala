package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.Session
import ar.noxit.paralleleditor.common.messages.ToKernel

trait ToKernelConverter {
    def convert(session: Session, toKernel: ToKernel): Any
}
