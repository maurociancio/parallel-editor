package ar.noxit.paralleleditor.kernel.remote

import java.net.ServerSocket
import scala.actors.Actor._

class KernelService {

    var shouldExit = false

    def init = {
        val socket = new ServerSocket(5000)

        while (!shouldExit) {
            val client = socket.accept

            actor {
                val in = client.getInputStream
                val out = client.getOutputStream

                out.write(in.read)

                client.close
            }
        }
    }
}