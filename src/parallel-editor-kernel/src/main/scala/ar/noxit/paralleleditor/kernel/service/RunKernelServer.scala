package ar.noxit.paralleleditor.kernel.service

import ar.noxit.paralleleditor.kernel.remote.{SocketKernelService}

object RunKernelServer {
    def main(args: Array[String]) {
        new SocketKernelService(5000).start
    }
}
