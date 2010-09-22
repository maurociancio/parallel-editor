package ar.noxit.paralleleditor.kernel.actors

import ar.noxit.paralleleditor.kernel._
import ar.noxit.paralleleditor.kernel.messages._
import scala.actors.Actor
import scala.actors.Actor._

class KernelActor(val kernel: Kernel) extends Actor {

    def act = {
        println("kernel actor started")
        var exit = false

        loopWhile(!exit) {
            println("choosing")
            react {
                case LoginRequest(username, caller) => {
                    println("login requested " + username)

                    // TODO catch user already login exception and send it to
                    // the caller, so he could pick a new name
                    val newSession = kernel.login(username)
                    caller ! LoginResponse(newSession)
                }

                case DocumentListRequest(session) => {
                    // ask kernel for the document list
                    val documentList = kernel.documentList

                    // return it to the caller
                    session notifyUpdate DocumentListResponse(documentList)
                }

                case NewDocumentRequest(session, title) => {
                    println("new document requested " + title + " from session " + session)

                    val docSession = kernel.newDocument(session, title)
                    session notifyUpdate NewDocumentResponse(docSession)
                }

                case _ => println("default")
            }
        }
        // la ejecución nunca llega acá
    }
}