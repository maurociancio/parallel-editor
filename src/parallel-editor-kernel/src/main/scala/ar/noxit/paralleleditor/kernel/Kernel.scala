package ar.noxit.paralleleditor.kernel

trait Kernel {

    def login(username: String): Session
    def newDocument(owner: Session, title: String, initialContent: String = ""): DocumentSession
    def documentList: List[String]
    //    def subscribe(session: Session, title: String)
}