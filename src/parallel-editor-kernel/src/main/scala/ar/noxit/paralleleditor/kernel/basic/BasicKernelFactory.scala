package ar.noxit.paralleleditor.kernel

import ar.noxit.paralleleditor.kernel.basic.BasicKernel

class BasicKernelFactory extends KernelFactory {

    def buildKernel = {
        new BasicKernel
    }
}