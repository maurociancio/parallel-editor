package ar.noxit.paralleleditor.kernel.remote

import actors.Actor
import java.net.Socket
import java.io.{ObjectOutput, ObjectOutputStream}
import ar.noxit.paralleleditor.kernel.messages._

class ClientService extends Actor {
    def act = {
        val socket = new Socket("localhost", 5000)
        val output = new OutputWriterActor(new ObjectOutputStream(socket.getOutputStream)).start

        // TODO close the socket
    }
}

// TODO change class' name
class OutputWriterActor(val output: ObjectOutput) extends Actor {
    def act = {
        println("sending login")
        output.writeObject(RemoteLogin("pepe"))
        println("login sent")
    }
}