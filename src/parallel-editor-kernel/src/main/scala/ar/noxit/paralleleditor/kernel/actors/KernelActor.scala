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
                case ("login", username: String, caller: Actor) => {
                    println("login requested " + username)

                    // TODO catch user already login exception and send it to
                    // the caller, so he could pick a new name
                    val newSession = kernel.login(username)
                    caller ! newSession
                }

                case ("doclist", caller: Actor) => {
                    // ask kernel for the document list
                    val documentList = kernel.documentList

                    // return it to the caller
                    caller ! ("doclist", documentList)
                }

                case ("newdoc", caller: Actor, session: Session, title: String) => {
                    println("new document requested " + title + " from session " + session)
                    val docSession = kernel.newDocument(session, title)
                    caller ! docSession
                }

                case "quit" => {
                    println("quit requested")
                    exit = true
                }

                case _ =>
                    println("default")
            }
        }
        // la ejecución nunca llega acá
    }
}