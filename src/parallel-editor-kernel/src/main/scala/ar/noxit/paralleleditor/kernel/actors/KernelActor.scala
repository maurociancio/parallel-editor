package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel._
import scala.actors.Actor
import scala.actors.Actor._

class KernelActor(val kernel: Kernel) extends Actor {

    def act = {
        println("kernel actor started")
        var exit = false

        loopWhile(!exit) {
            println("choosing")
            react {
                case ("login", username: String, caller: Actor) =>
                    println("login requested " + username)

                    // TODO catch user already login exception and send it to
                    // the caller, so he could pick a new name
                    val newSession = kernel.login(username)
                    caller ! newSession

                case ("newdoc", session: Session, name: String) =>
                    println("new document requested " + name + " from session " + session)

                case "quit" =>
                    println("quit requested")
                    exit = true

                case _ =>
                    println("default")
            }
        }
        // la ejecución nunca llega acá
    }
}