package ar.noxit.paralleleditor.kernel

trait Kernel {

    def login(username: String) : Session
    def logout(session: Session)
    def newDocument(owner: Session, title: String, initialContent: String = "") : DocumentHandler
//    def suscribe(session: Session, title: String)
}