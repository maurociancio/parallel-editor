package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel.DocumentSession
import ar.noxit.paralleleditor.kernel.callback.ActorCallback
import ar.noxit.paralleleditor.kernel.Session
import scala.actors.Actor

class ClientActor(val kernel: Actor, val connectionClient: Actor) extends Actor {

    var docSessions: List[DocumentSession] = List()

    def act = {
        println("waiting for username")
        val username = receive {
            case username: String => username
        }
        println("username received " + username)

        // logging into the kernel
        kernel ! ("login", username, this)

        // we expect a non null session in order to continue
        val session = receive {
            case session: Session if session != null => {
                println("session received")
                session
            }
        }
        println(session)

        session.installOnUpdateCallback(new ActorCallback(connectionClient))

        var exit = false
        loopWhile(!exit) {
            react {
                case ("newdoc", title: String) => {
                    println("new doc requested " + title)

                    kernel ! ("newdoc", this, session, title)
                    receive {
                        case docSession: DocumentSession => {
                            println("received doc session")

                            docSessions = docSession :: docSessions
                            connectionClient ! ("newdoc", title)
                        }
                    }
                }

                case "doclist" => {
                    println("doclist requested")

                    kernel ! ("doclist", connectionClient)
                }

                case "logout" => {
                    println("logout requested")
                    session.logout

                    exit = true
                }
            }
        }
    }
}