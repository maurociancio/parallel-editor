package ar.noxit.paralleleditor.kernel.remote

import java.net.ServerSocket
import ar.noxit.paralleleditor.kernel.basic.BasicKernel
import ar.noxit.paralleleditor.kernel.actors.{KernelActor, ClientActor}
import actors.Actor

class KernelService extends Actor {
    var sExit = false
    val server = new ServerSocket(5000)
    val kernel = new BasicKernel
    val ka = new KernelActor(kernel)

    def act = {
        while (!sExit) {
            // client socket
            val clientSocket = server.accept

            // new client
            val aClient = new RemoteClient(clientSocket)

            // output actor
            val outputActor = aClient.output

            // client actor
            val clientActor = new ClientActor(ka, outputActor).start

            // set the target actor
            aClient.target = clientActor

            // start the new client
            aClient.start
        }
    }
}