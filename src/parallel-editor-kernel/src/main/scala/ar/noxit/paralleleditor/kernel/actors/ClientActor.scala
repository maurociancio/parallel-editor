package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import ar.noxit.paralleleditor.kernel.Session
import scala.actors.Actor

class ClientActor(val kernel: Actor, val username: String) extends Actor {

    def act = {
        kernel ! ("login", username, this)

        // we expect a non null session in order to continue
        val session = receive {
            case session: Session if session != null => {
                println("session received")
                session
            }
        }
        println(session)

        // TODO install the callback that send informacion to the user
        session.installOnUpdateCallback(new ActorCallback(this))
    }
}