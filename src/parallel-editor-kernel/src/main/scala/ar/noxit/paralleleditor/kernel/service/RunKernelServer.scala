package ar.noxit.paralleleditor.kernel.service

import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.remote.KernelServer

object RunKernelServer {
    def main(args: Array[String]) {
        new KernelServer().start
    }
}
