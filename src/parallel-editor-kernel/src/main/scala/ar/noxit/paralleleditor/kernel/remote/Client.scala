package ar.noxit.paralleleditor.kernel.remote

import java.net.Socket
import scala.actors.Actor
import scala.actors.Actor._
import java.io._

class RemoteClient(val socket: Socket) {
    var initialized = false
    var input: Actor = _
    val output = new OutputActor(new ObjectOutputStream(socket.getOutputStream))
    var target: Actor = _

    def start : Unit = {
        checkTarget
        markAsInitialized

        input = new InputActor(target, new ObjectInputStream(socket.getInputStream))
        output.start
        input.start
    }

    def stop = {
        checkInitialized

        socket close
    }

    private def checkInitialized = {
        if (!initialized)
            throw new IllegalStateException("not initialized")
    }

    private def markAsInitialized = {
        if (initialized)
            throw new IllegalStateException("already initialized")
        initialized = true
    }

    private def checkTarget: Unit = {
        if (target == null)
            throw new IllegalStateException("target actor cannot be null")
    }
}

class InputActor(val target: Actor, val input: ObjectInput) extends Actor {
    override def act = {
        var exit = false
        while (!exit) {
            val in: Any = input.readObject()
            println("received " + in)
            target ! in
        }
    }
}

class OutputActor(val output: ObjectOutput) extends Actor {
    override def act = {
        var exit = false
        loopWhile(!exit) {
            react {
                case message: Any =>
                    output writeObject message
            }
        }
    }
}