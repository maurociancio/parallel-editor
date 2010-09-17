package ar.noxit.paralleleditor.kernel.remote

import java.io.{InputStream,OutputStream}
import java.net.Socket
import scala.actors.Actor
import scala.actors.Actor._

class Client(val socket: Socket) extends Actor {
    val input = new InputActor(this, socket.getInputStream)
    val output = new OutputActor(this, socket.getOutputStream)

    def act = {
        input.start
        output.start

        while (true) {
            receive {
                case n:Int => output ! n
            }
        }
    }
}

class InputActor(val client: Actor, val input: InputStream) extends Actor {
    override def act = {
        while (true) {
            val in = input.read
            client ! in
        }
    }
}

class OutputActor(val client: Actor, val output: OutputStream) extends Actor {
    override def act = {
        while (true) {
            receive {
                case n: Int => output.write(n)
            }
        }
    }
}