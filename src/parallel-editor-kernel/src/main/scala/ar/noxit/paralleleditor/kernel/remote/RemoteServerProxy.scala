package ar.noxit.paralleleditor.kernel.remote

import actors.Actor
import java.net.Socket
import java.io.{ObjectOutput, ObjectOutputStream}
import ar.noxit.paralleleditor.kernel.messages._

/**
 * Esta clase representa al servidor remoto desde el punto de vista del cliente.
 */
class RemoteServerProxy(socket: Socket) {
    val output = new OutputWriterActor(new ObjectOutputStream(socket.getOutputStream)).start
}

// TODO change class' name
class OutputWriterActor(val output: ObjectOutput) extends Actor {
    def act = {
        println("sending login")
        output.writeObject(RemoteLogin("pepe"))
        println("login sent")
    }
}